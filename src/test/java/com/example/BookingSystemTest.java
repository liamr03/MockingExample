package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingSystemTest {

    private BookingSystem bookingSystem;
    private RoomRepository roomRepository;
    private NotificationService notificationService;
    private TimeProvider timeProvider;

    @BeforeEach
    void setUp() {
        roomRepository = mock(RoomRepository.class);
        notificationService = mock(NotificationService.class);
        timeProvider = mock(TimeProvider.class);
        bookingSystem = new BookingSystem(timeProvider, roomRepository, notificationService);
    }

    @Test
    void bookRoomSuccessfully() throws NotificationException {
        String roomId = "room1";
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);

        Room room = mock(Room.class);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(room.isAvailable(startTime, endTime)).thenReturn(true);
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());

        boolean result = bookingSystem.bookRoom(roomId, startTime, endTime);


        assertThat(result).isTrue();
        verify(room).addBooking(any(Booking.class));
        verify(roomRepository).save(room);
        verify(notificationService).sendBookingConfirmation(any(Booking.class));
    }

    @Test
    void bookRoomFailsWhenRoomNotAvailable() throws NotificationException {
        String roomId = "room1";
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);

        Room room = mock(Room.class);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(room.isAvailable(startTime, endTime)).thenReturn(false);
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());

        boolean result = bookingSystem.bookRoom(roomId, startTime, endTime);

        assertThat(result).isFalse();
        verify(room, never()).addBooking(any(Booking.class));
        verify(roomRepository, never()).save(any(Room.class));
        verify(notificationService, never()).sendBookingConfirmation(any(Booking.class));
    }

    @Test
    void bookRoomThrowsExceptionForInvalidArguments() {
        // Test for null startTime
        assertThatThrownBy(() -> bookingSystem.bookRoom("room1", null, LocalDateTime.now().plusHours(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bokning kräver giltiga start- och sluttider samt rum-id");

        // Test for null endTime
        assertThatThrownBy(() -> bookingSystem.bookRoom("room1", LocalDateTime.now(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bokning kräver giltiga start- och sluttider samt rum-id");

        // Test for null roomId
        assertThatThrownBy(() -> bookingSystem.bookRoom(null, LocalDateTime.now(), LocalDateTime.now().plusHours(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bokning kräver giltiga start- och sluttider samt rum-id");
    }


    @Test
    void bookRoomFailsIfStartTimeIsInPast() {
        String roomId = "room1";
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);

        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());

        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenEndTimeIsBeforeStartTime() {
        String roomId = "room1";
        LocalDateTime now = LocalDateTime.of(2025, 1, 30, 12, 0);
        LocalDateTime startTime = now.plusHours(1);
        LocalDateTime endTime = startTime.minusHours(1);

        when(timeProvider.getCurrentTime()).thenReturn(now);

        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sluttid måste vara efter starttid");
    }


    @Test
    void getAvailableRoomsReturnsCorrectRooms() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);

        Room room1 = mock(Room.class);
        Room room2 = mock(Room.class);
        Room room3 = mock(Room.class);

        when(room1.isAvailable(startTime, endTime)).thenReturn(true);
        when(room2.isAvailable(startTime, endTime)).thenReturn(false);
        when(room3.isAvailable(startTime, endTime)).thenReturn(true);

        when(roomRepository.findAll()).thenReturn(List.of(room1, room2, room3));

        List<Room> availableRooms = bookingSystem.getAvailableRooms(startTime, endTime);

        assertThat(availableRooms).hasSize(2);
        assertThat(availableRooms.contains(room1)).isTrue();
        assertThat(availableRooms.contains(room3)).isTrue();
        assertThat(availableRooms.contains(room2)).isFalse();
    }

    @Test
    void getAvailableRoomsThrowsExceptionForInvalidArguments() {
        // Test for null startTime
        assertThatThrownBy(() -> bookingSystem.getAvailableRooms(null, LocalDateTime.now().plusHours(1)))
                .isInstanceOf(IllegalArgumentException.class);

        // Test for null endTime
        assertThatThrownBy(() -> bookingSystem.getAvailableRooms(LocalDateTime.now(), null))
                .isInstanceOf(IllegalArgumentException.class);

        // Test for endTime before startTime
        assertThatThrownBy(() -> bookingSystem.getAvailableRooms(LocalDateTime.now().plusHours(1), LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class);
    }



    @Test
    void cancelBookingSuccessfully() throws NotificationException {
        String bookingId = "booking1";
        LocalDateTime futureStartTime = LocalDateTime.now().plusHours(1);
        Booking booking = mock(Booking.class);

        Room room = mock(Room.class);
        when(room.hasBooking(bookingId)).thenReturn(true);
        when(room.getBooking(bookingId)).thenReturn(booking);
        when(booking.getStartTime()).thenReturn(futureStartTime);

        when(roomRepository.findAll()).thenReturn(List.of(room));
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());

        boolean result = bookingSystem.cancelBooking(bookingId);

        assertThat(result).isTrue();
        verify(room).removeBooking(bookingId);
        verify(roomRepository).save(room);
        verify(notificationService).sendCancellationConfirmation(booking);
    }

    @Test
    void cancelBookingFailsForNonExistentBooking() {
        String bookingId = "nonExistentBooking";

        when(roomRepository.findAll()).thenReturn(List.of());

        boolean result = bookingSystem.cancelBooking(bookingId);

        assertThat(result).isFalse();
    }

    @Test
    void cancelBookingFailsForNullBooking() {

        when(roomRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> bookingSystem.cancelBooking(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cancelBookingFailsForStartedBooking() {
        String bookingId = "booking1";
        LocalDateTime pastStartTime = LocalDateTime.now().minusHours(1);
        Booking booking = mock(Booking.class);

        Room room = mock(Room.class);
        when(room.hasBooking(bookingId)).thenReturn(true);
        when(room.getBooking(bookingId)).thenReturn(booking);
        when(booking.getStartTime()).thenReturn(pastStartTime);

        when(roomRepository.findAll()).thenReturn(List.of(room));
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());

        assertThatThrownBy(() -> bookingSystem.cancelBooking(bookingId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Kan inte avboka påbörjad eller avslutad bokning");
    }

}
