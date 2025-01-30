package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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


        assertTrue(result);
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

        assertFalse(result);
        verify(room, never()).addBooking(any(Booking.class));
        verify(roomRepository, never()).save(any(Room.class));
        verify(notificationService, never()).sendBookingConfirmation(any(Booking.class));
    }

    @Test
    void bookRoomThrowsExceptionForInvalidArguments() {
        // Test for null startTime
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () ->
                bookingSystem.bookRoom("room1", null, LocalDateTime.now().plusHours(1))
        );
        assertEquals("Bokning kräver giltiga start- och sluttider samt rum-id", exception1.getMessage());

        // Test for null endTime
        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () ->
                bookingSystem.bookRoom("room1", LocalDateTime.now(), null)
        );
        assertEquals("Bokning kräver giltiga start- och sluttider samt rum-id", exception2.getMessage());

        // Test for null roomId
        IllegalArgumentException exception3 = assertThrows(IllegalArgumentException.class, () ->
                bookingSystem.bookRoom(null, LocalDateTime.now(), LocalDateTime.now().plusHours(1))
        );
        assertEquals("Bokning kräver giltiga start- och sluttider samt rum-id", exception3.getMessage());
    }


    @Test
    void bookRoomFailsIfStartTimeIsInPast() {
        String roomId = "room1";
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);

        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());

        assertThrows(IllegalArgumentException.class, () -> bookingSystem.bookRoom(roomId, startTime, endTime));
    }

    @Test
    void shouldThrowExceptionWhenEndTimeIsBeforeStartTime() {
        String roomId = "room1";
        LocalDateTime now = LocalDateTime.of(2025, 1, 30, 12, 0);
        LocalDateTime startTime = now.plusHours(1);
        LocalDateTime endTime = startTime.minusHours(1);

        when(timeProvider.getCurrentTime()).thenReturn(now);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.bookRoom(roomId, startTime, endTime);
        });

        assertEquals("Sluttid måste vara efter starttid", exception.getMessage());
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

        assertEquals(2, availableRooms.size());
        assertTrue(availableRooms.contains(room1));
        assertTrue(availableRooms.contains(room3));
        assertFalse(availableRooms.contains(room2));
    }

    @Test
    void getAvailableRoomsThrowsExceptionForInvalidArguments() {
        // Test for null startTime
        assertThrows(IllegalArgumentException.class, () ->
                bookingSystem.getAvailableRooms(null, LocalDateTime.now().plusHours(1))
        );

        // Test for null endTime
        assertThrows(IllegalArgumentException.class, () ->
                bookingSystem.getAvailableRooms(LocalDateTime.now(), null)
        );

        // Test for endTime before startTime
        assertThrows(IllegalArgumentException.class, () ->
                bookingSystem.getAvailableRooms(LocalDateTime.now().plusHours(1), LocalDateTime.now())
        );
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

        assertTrue(result);
        verify(room).removeBooking(bookingId);
        verify(roomRepository).save(room);
        verify(notificationService).sendCancellationConfirmation(booking);
    }

    @Test
    void cancelBookingFailsForNonExistentBooking() {
        String bookingId = "nonExistentBooking";

        when(roomRepository.findAll()).thenReturn(List.of());

        boolean result = bookingSystem.cancelBooking(bookingId);

        assertFalse(result);
    }

    @Test
    void cancelBookingFailsForNullBooking() {
        String bookingId = null;

        when(roomRepository.findAll()).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> bookingSystem.cancelBooking(bookingId));
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

        assertThrows(IllegalStateException.class, () -> bookingSystem.cancelBooking(bookingId));
    }

}
