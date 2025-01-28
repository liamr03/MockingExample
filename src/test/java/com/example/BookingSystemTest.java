package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
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
    void getAvailableRooms() {
    }

    @Test
    void cancelBooking() {
    }
}
