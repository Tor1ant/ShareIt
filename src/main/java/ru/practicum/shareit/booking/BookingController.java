package ru.practicum.shareit.booking;

import static ru.practicum.shareit.item.ItemController.CUSTOM_USER_ID_HEADER;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.BadRequestException;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Booking add(@RequestHeader(CUSTOM_USER_ID_HEADER) Long bookerId,
            @RequestBody @Valid BookingInputDto bookingInputDto) {
        bookingInputDto.setStatus(BookingStatus.WAITING);
        if (bookingInputDto.getEnd().isBefore(bookingInputDto.getStart())
                || bookingInputDto.getStart().equals(bookingInputDto.getEnd())) {
            log.warn("Окончание бронирования не может быть раньше начала.");
            throw new BadRequestException("Окончание бронирования не может быть раньше начала.");
        }
        Booking booking = bookingService.createBooking(bookingInputDto, bookerId);
        log.info(booking + " сохранен в базу данных");
        return booking;
    }

    @PatchMapping("{bookingId}")
    public Booking setApprove(@RequestHeader(CUSTOM_USER_ID_HEADER) Long ownerId, @PathVariable Long bookingId,
            @RequestParam boolean approved) {
        log.info("Booking с id " + bookingId + " подтверждён");
        return bookingService.setApproved(ownerId, bookingId, approved);
    }

    @GetMapping("{bookingId}")
    public Booking getBooking(
            @RequestHeader(CUSTOM_USER_ID_HEADER) Long userID, @PathVariable Long bookingId) {
        Booking booking = bookingService.getBooking(userID, bookingId);
        log.info(booking + " передано пользователю");
        return booking;
    }

    @GetMapping()
    public List<Booking> getAllUserBookings(@RequestHeader(CUSTOM_USER_ID_HEADER) Long userID,
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        List<Booking> bookings = bookingService.getAllBookings(userID, state);
        log.info(bookings + " переданы пользователю");
        return bookings;
    }

    @GetMapping("/owner")
    public List<Booking> getAllOwnersBookings(@RequestHeader(CUSTOM_USER_ID_HEADER) Long userID,
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        List<Booking> bookings = bookingService.getAllOwnerBookings(userID, state);
        log.info(bookings + " переданы владельцу вещи");
        return bookings;
    }
}
