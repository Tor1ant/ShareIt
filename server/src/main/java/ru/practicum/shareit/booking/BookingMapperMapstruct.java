package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingIdAndBookerDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapperMapstruct {

    Booking bookingDtoToBooking(BookingInputDto bookingInputDto);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingIdAndBookerDto bookingToBookingDoIdAndOwnerId(Booking booking);
}
