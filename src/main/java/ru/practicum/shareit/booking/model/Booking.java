package ru.practicum.shareit.booking.model;

/**
 * TODO Sprint add-bookings.
 */
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;

@Builder
@Data
public class Booking {
    private LocalDateTime startBooking;
    private LocalDateTime endBooking;
    private Duration bookingTIme;
    private boolean isAcceptBooking;
    private final Item item;
    private BookingStatus bookingStatus;
    private final Map<Long, String> reviews;
}
