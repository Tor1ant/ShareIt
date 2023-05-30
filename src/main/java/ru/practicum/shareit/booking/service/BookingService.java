package ru.practicum.shareit.booking.service;

import java.util.List;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingService {

    Booking createBooking(BookingInputDto bookingInputDto, Long userId);

    Booking setApproved(Long ownerId, Long bookingId, boolean approved);

    Booking getBooking(Long id, Long bookingId);

    List<Booking> getAllBookings(Long userID, String state);

    List<Booking> getAllOwnerBookings(Long userID, String state);
}
