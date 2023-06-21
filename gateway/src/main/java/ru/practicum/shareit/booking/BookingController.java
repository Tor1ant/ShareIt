package ru.practicum.shareit.booking;

import static ru.practicum.shareit.item.ItemController.CUSTOM_USER_ID_HEADER;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.shareit.exceptions.BadRequestException;


@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(CUSTOM_USER_ID_HEADER) Long bookerId,
            @RequestBody @Valid BookingInputDto bookingInputDto) {
        bookingInputDto.setStatus(BookingStatus.WAITING);
        if (bookingInputDto.getEnd().isBefore(bookingInputDto.getStart())
                || bookingInputDto.getStart().equals(bookingInputDto.getEnd())) {
            log.warn("Окончание бронирования не может быть раньше начала.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Окончание бронирования не может быть раньше начала.");
        }
        ResponseEntity<Object> booking = bookingClient.createBooking(bookingInputDto, bookerId);
        log.info(booking + " сохранен в базу данных");
        return booking;
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> setApprove(@RequestHeader(CUSTOM_USER_ID_HEADER) Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved) {
        log.info("Booking с id " + bookingId + " подтверждён");
        return bookingClient.setApproved(ownerId, bookingId, approved);
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<Object> getBooking(
            @RequestHeader(CUSTOM_USER_ID_HEADER) Long userID, @PathVariable Long bookingId) {
        ResponseEntity<Object> booking = bookingClient.getBooking(userID, bookingId);
        log.info(booking + " передано пользователю");
        return booking;
    }

    @GetMapping()
    public ResponseEntity<Object> getAllUserBookings(@RequestHeader(CUSTOM_USER_ID_HEADER) Long userID,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "0") Long from,
            @RequestParam(required = false, defaultValue = "10") Long size) {
        isPageableValidation(from, size);

        ResponseEntity<Object> bookings = bookingClient.getAllBookings(userID, state, from, size);
        log.info(bookings + " переданы пользователю");
        return bookings;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllOwnersBookings(@RequestHeader(CUSTOM_USER_ID_HEADER) Long userID,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "0") Long from,
            @RequestParam(required = false, defaultValue = "10") Long size) {
        isPageableValidation(from, size);

        ResponseEntity<Object> bookings = bookingClient.getAllOwnerBookings(userID, state, from, size);
        log.info(bookings + " переданы владельцу вещи");
        return bookings;
    }

    private void isPageableValidation(Long from, Long size) {
        if (from < 0) {
            throw new BadRequestException("Индекс первого элемента не может быть меньше нуля");
        }
        if (size < 0 || size == 0) {
            throw new BadRequestException("Количество элементов для отображения не может быть меньше или равно нулю");
        }
    }
}
