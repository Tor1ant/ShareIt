package ru.practicum.shareit.booking.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapperMapstruct;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapperMapstruct mapper = Mappers.getMapper(BookingMapperMapstruct.class);
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Booking createBooking(BookingInputDto bookingInputDto, Long userId) {
        Optional<Item> item = itemRepository.findById(bookingInputDto.getItemId());

        if (!item.isPresent()) {
            throw new NotFoundException("Вещи не существует.");
        }

        if (item.get().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Вы не можете забронировать свою вещь.");
        }
        if (!item.get().getAvailable()) {
            throw new BadRequestException("Вещь уже забронирована.");
        }
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new NotFoundException("Пользователь посылающий запрос не найден.");
        }
        Booking booking = mapper.bookingDtoToBooking(bookingInputDto);
        booking.setBooker(user.get());
        booking.setItem(item.get());
        log.info(booking + "\n" + "добавлен в базу данных.");
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking setApproved(Long ownerId, Long bookingId, boolean approved) {
        Optional<Booking> optionalBooking = bookingRepository.findBookingByIdAndItemOwnerId(bookingId, ownerId);
        if (!optionalBooking.isPresent()) {
            throw new NotFoundException("Такого бронирования не существует.");
        }
        Booking booking = optionalBooking.get();
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BadRequestException("Бронирование уже одобрено.");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        booking = bookingRepository.save(booking);
        log.info(booking + "\n" + "APPROVED");
        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBooking(Long userId, Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findByIdWithItemAndBooker(bookingId, userId);
        if (!optionalBooking.isPresent()) {
            throw new NotFoundException("Такого бронирования не существует.");

        }
        return optionalBooking.get();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllBookings(Long userID, String state) {
        Optional<List<Booking>> bookingList;
        Optional<User> user = userRepository.findById(userID);

        if (!user.isPresent()) {
            throw new NotFoundException("Пользователь не найден");
        }
        switch (state) {
            case "ALL":
                bookingList = bookingRepository.findBookingsByBookerIdOrderByEndDesc(userID);
                log.info(bookingList + " получен из базы данных.");
                break;
            case "CURRENT":
                bookingList = bookingRepository.findAllCurrentByUserIdAndSortByDesc(userID);
                log.info(bookingList + " получен из базы данных.");
                break;
            case "PAST":
                bookingList = bookingRepository.findAllPastByUserIdAndSortByDesc(userID);
                log.info(bookingList + " получен из базы данных.");
                break;
            case "FUTURE":
                bookingList = bookingRepository.findAllFutureByUserIdAndSortByDesc(userID);
                log.info(bookingList + " получен из базы данных.");
                break;
            case "WAITING":
                bookingList = bookingRepository.findBookingsByStatusAndBookerIdOrderByEndDesc(BookingStatus.WAITING,
                        userID);
                log.info(bookingList + " получен из базы данных.");
                break;
            case "REJECTED":
                bookingList = bookingRepository.findBookingsByStatusAndBookerIdOrderByEndDesc(BookingStatus.REJECTED,
                        userID);
                log.info(bookingList + " получен из базы данных.");
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        if (!bookingList.isPresent()) {
            throw new NotFoundException("Бронирования не найдены");
        }
        return bookingList.get();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllOwnerBookings(Long ownerId, String state) {
        Optional<List<Booking>> bookingList;
        Optional<User> user = userRepository.findById(ownerId);

        if (!user.isPresent()) {
            throw new NotFoundException("Пользователь не найден");
        }
        switch (state) {
            case "ALL":
                bookingList = bookingRepository.findBookingsByItemOwnerIdOrderByEndDesc(ownerId);
                log.info(bookingList + " получен из базы данных.");
                break;
            case "CURRENT":
                bookingList = bookingRepository.findAllCurrentByOwnerIdAndSortByDesc(ownerId);
                log.info(bookingList + " получен из базы данных.");
                break;
            case "PAST":
                bookingList = bookingRepository.findAllPastByOwnerIdAndSortByDesc(ownerId);
                log.info(bookingList + " получен из базы данных.");
                break;
            case "FUTURE":
                bookingList = bookingRepository.findAllFutureByOwnerIdAndSortByDesc(ownerId);
                log.info(bookingList + " получен из базы данных.");
                break;
            case "WAITING":
                bookingList = bookingRepository.findBookingsByStatusAndItemOwnerIdOrderByEndDesc(BookingStatus.WAITING,
                        ownerId);
                log.info(bookingList + " получен из базы данных.");
                break;
            case "REJECTED":
                bookingList = bookingRepository.findBookingsByStatusAndItemOwnerIdOrderByEndDesc(BookingStatus.REJECTED,
                        ownerId);
                log.info(bookingList + " получен из базы данных.");
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        if (!bookingList.isPresent()) {
            throw new NotFoundException("Бронирования не найдены");
        }
        return bookingList.get();
    }
}
