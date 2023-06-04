package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.BadRequestException;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.item.ItemController.CUSTOM_USER_ID_HEADER;

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
                                            @RequestParam(required = false, defaultValue = "ALL") String state,
                                            @RequestParam(required = false, defaultValue = "0") Long from,
                                            @RequestParam(required = false, defaultValue = "10") Long size) {
        pageableValidation(from, size);

        List<Booking> bookings = bookingService.getAllBookings(userID, state, from, size);
        log.info(bookings + " переданы пользователю");
        return bookings;
    }

    @GetMapping("/owner")
    public List<Booking> getAllOwnersBookings(@RequestHeader(CUSTOM_USER_ID_HEADER) Long userID,
                                              @RequestParam(required = false, defaultValue = "ALL") String state,
                                              @RequestParam(required = false, defaultValue = "0") Long from,
                                              @RequestParam(required = false, defaultValue = "10") Long size) {

        pageableValidation(from, size);
        List<Booking> bookings = bookingService.getAllOwnerBookings(userID, state, from, size);
        log.info(bookings + " переданы владельцу вещи");
        return bookings;
    }

    private void pageableValidation(Long from, Long size) {
        if (from < 0) {
            throw new BadRequestException("Индекс первого элемента не может быть меньше нуля");
        }
        if (size < 0 || size == 0) {
            throw new BadRequestException("Количество элементов для отображения не может быть меньше или равно нулю");
        }
    }
}
