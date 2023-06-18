package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking createBooking(BookingInputDto bookingInputDto, Long userId);

    Booking setApproved(Long ownerId, Long bookingId, boolean approved);

    Booking getBooking(Long id, Long bookingId);

    List<Booking> getAllBookings(Long userID, String state, Long from, Long size);

    List<Booking> getAllOwnerBookings(Long userID, String state, Long from, Long size);
}
