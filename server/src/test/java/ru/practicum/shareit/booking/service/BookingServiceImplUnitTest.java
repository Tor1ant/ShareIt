package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplUnitTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    private Item item;
    private Item item2;
    private Item item3;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("testUser");
        user.setEmail("test@email.ru");
        user.setId(1L);
        item = new Item(1L, user, "testItem", "testDescription", true,
                new ItemRequest());
        item2 = new Item(2L, user, "testItem2", "testDescription2", false,
                new ItemRequest());
        item3 = new Item(3L, user, "testItem3", "testDescription3", true,
                new ItemRequest());
    }

    @DisplayName("Проверка создания бронирования несуществующей вещи")
    @Test
    void createBookingWithoutItemTest() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(BookingInputDto.builder().itemId(1L).build(), 1L));
    }

    @DisplayName("Проверка создания бронирования  вещи её владельцем")
    @Test
    void createBookingByOwner() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(BookingInputDto.builder().itemId(1L).build(), 1L));
    }

    @DisplayName("Проверка создания бронирования  вещи несуществующим пользователем")
    @Test
    void createBookingByNotExistUser() {

        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item3));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(BookingInputDto.builder().itemId(1L).build(), 2L));
    }

    @DisplayName("Проверка создания бронирования  уже забронированной вещи пользователем")
    @Test
    void createBookingToBookedItem() {
        item2.getOwner().setId(3L);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item2));
        Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.createBooking(BookingInputDto.builder().itemId(1L).build(), 2L));
    }

    @DisplayName("Проверка создания бронирования  вещи пользователем")
    @Test
    void createBookingByUserTest() {
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(1L)
                .status(BookingStatus.APPROVED)
                .build();
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);

        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item3));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        Assertions.assertEquals(item, bookingService.createBooking(bookingInputDto, 2L).getItem());
    }

    @DisplayName("Проверка подтверждения несуществующего бронирования")
    @Test
    void setApprovedNotFoundExceptionTest() {
        Mockito.when(bookingRepository.findBookingByIdAndItemOwnerId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.setApproved(1L, 1L, true));
    }

    @Test
    @DisplayName("Проверка установки статуса одобрения бронирования")
    void setApprovedStatusApproved() {
        Long ownerId = 1L;
        Long bookingId = 2L;
        boolean approved = true;
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);

        Mockito.when(bookingRepository.findBookingByIdAndItemOwnerId(bookingId, ownerId))
                .thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);

        Booking result = bookingService.setApproved(ownerId, bookingId, approved);

        Assertions.assertEquals(BookingStatus.APPROVED, result.getStatus());
        Mockito.verify(bookingRepository, Mockito.times(1)).save(booking);
    }


    @Test
    @DisplayName("Проверка установки статуса отклонения бронирования")
    void setApprovedStatusRejected() {
        Long ownerId = 1L;
        Long bookingId = 2L;
        boolean approved = false;
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);

        Mockito.when(bookingRepository.findBookingByIdAndItemOwnerId(bookingId, ownerId))
                .thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);

        Booking result = bookingService.setApproved(ownerId, bookingId, approved);

        Assertions.assertEquals(BookingStatus.REJECTED, result.getStatus());
        Mockito.verify(bookingRepository, Mockito.times(1)).save(booking);
    }

    @Test
    @DisplayName("Проверка исключения при попытке одобрить уже одобренное бронирование")
    void setApprovedBookingAlreadyApproved() {
        Long ownerId = 1L;
        Long bookingId = 2L;
        boolean approved = true;
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.APPROVED);

        Mockito.when(bookingRepository.findBookingByIdAndItemOwnerId(bookingId, ownerId)).thenReturn(Optional
                .of(booking));

        Assertions.assertThrows(BadRequestException.class, () -> bookingService
                .setApproved(ownerId, bookingId, approved));
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Получение существующего бронирования")
    void getBookingBookingExists() {
        Long userId = 1L;
        Long bookingId = 2L;

        Booking booking = new Booking();

        Mockito.when(bookingRepository.findByIdWithItemAndBooker(bookingId, userId)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBooking(userId, bookingId);

        Assertions.assertEquals(booking, result);
    }


    @Test
    @DisplayName("Получение несуществующего бронирования")
    void getBookingBookingNotFound() {
        Long userId = 1L;
        Long bookingId = 2L;

        Mockito.when(bookingRepository.findByIdWithItemAndBooker(bookingId, userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getBooking(userId, bookingId));
    }

    @Test
    @DisplayName("Получение всех бронирований")
    void getAllBookingsAllState() {
        Long userId = 1L;
        String state = "ALL";
        Long from = 0L;
        Long size = 10L;

        User user = new User();
        List<Booking> bookingList = new ArrayList<>();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findBookingsByBookerId(userId, PageRequest.of(0, 10,
                        Sort.by("end").descending())))
                .thenReturn(Optional.of(bookingList));

        List<Booking> result = bookingService.getAllBookings(userId, state, from, size);

        Assertions.assertEquals(bookingList, result);
    }

    @Test
    @DisplayName("Получение текущих бронирований")
    void getAllBookingsCurrentState() {
        Long userId = 1L;
        String state = "CURRENT";
        Long from = 0L;
        Long size = 10L;

        User user = new User();
        List<Booking> bookingList = new ArrayList<>();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllCurrentByUserIdAndSortByDesc(userId, PageRequest.of(0, 10,
                        Sort.by("end").descending())))
                .thenReturn(Optional.of(bookingList));

        List<Booking> result = bookingService.getAllBookings(userId, state, from, size);

        Assertions.assertEquals(bookingList, result);
    }

    @Test
    @DisplayName("Получение бронирований для несуществующего пользователя")
    void getAllBookingsUserNotFound() {
        Long userId = 1L;
        String state = "ALL";
        Long from = 0L;
        Long size = 10L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getAllBookings(userId, state, from, size));
    }

    @Test
    @DisplayName("Получение пустого списка бронирований")
    void getAllBookingsBookingsNotFound() {
        Long userId = 1L;
        String state = "ALL";
        Long from = 0L;
        Long size = 10L;

        User user = new User();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findBookingsByBookerId(userId, PageRequest.of(0, 10,
                        Sort.by("end").descending())))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getAllBookings(userId, state, from, size));
    }

    @Test
    @DisplayName("Получение бронирований с неизвестным состоянием")
    void getAllBookingsUnknownState() {
        Long userId = 1L;
        String state = "UNSUPPORTED_STATUS";
        Long from = 0L;
        Long size = 10L;

        User user = new User();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Assertions.assertThrows(BadRequestException.class, () -> bookingService
                .getAllBookings(userId, state, from, size));
    }

    @Test
    @DisplayName("Получение прошлых бронирований")
    void getAllBookingsPastState() {
        Long userId = 1L;
        String state = "PAST";
        Long from = 0L;
        Long size = 10L;

        User user = new User();
        List<Booking> bookingList = new ArrayList<>();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllPastByUserIdAndSortByDesc(userId,
                        PageRequest.of(0, 10, Sort.by("end").descending())))
                .thenReturn(Optional.of(bookingList));

        List<Booking> result = bookingService.getAllBookings(userId, state, from, size);

        Assertions.assertEquals(bookingList, result);
    }

    @Test
    @DisplayName("Получение будущих бронирований")
    void getAllBookingsFutureState() {
        Long userId = 1L;
        String state = "FUTURE";
        Long from = 0L;
        Long size = 10L;

        User user = new User();
        List<Booking> bookingList = new ArrayList<>();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllFutureByUserIdAndSortByDesc(userId,
                        PageRequest.of(0, 10, Sort.by("end").descending())))
                .thenReturn(Optional.of(bookingList));

        List<Booking> result = bookingService.getAllBookings(userId, state, from, size);

        Assertions.assertEquals(bookingList, result);
    }

    @Test
    @DisplayName("Получение ожидающих бронирований")
    void getAllBookingsWaitingState() {
        Long userId = 1L;
        String state = "WAITING";
        Long from = 0L;
        Long size = 10L;

        User user = new User();
        List<Booking> bookingList = new ArrayList<>();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findBookingsByStatusAndBookerIdOrderByEndDesc(BookingStatus.WAITING, userId,
                        PageRequest.of(0, 10, Sort.by("end").descending())))
                .thenReturn(Optional.of(bookingList));

        List<Booking> result = bookingService.getAllBookings(userId, state, from, size);

        Assertions.assertEquals(bookingList, result);
    }

    @Test
    @DisplayName("Получение отклоненных бронирований")
    void getAllBookingsRejectedState() {
        Long userId = 1L;
        String state = "REJECTED";
        Long from = 0L;
        Long size = 10L;

        User user = new User();
        List<Booking> bookingList = new ArrayList<>();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findBookingsByStatusAndBookerIdOrderByEndDesc(BookingStatus.REJECTED, userId,
                        PageRequest.of(0, 10, Sort.by("end").descending())))
                .thenReturn(Optional.of(bookingList));

        List<Booking> result = bookingService.getAllBookings(userId, state, from, size);

        Assertions.assertEquals(bookingList, result);
    }

    @Test
    @DisplayName("Получение текущих бронирований владельца")
    void getAllOwnerBookings_CurrentState() {
        Long ownerId = 1L;
        String state = "CURRENT";
        Long from = 0L;
        Long size = 10L;

        User user = new User();
        List<Booking> bookingList = new ArrayList<>();

        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllCurrentByOwnerIdAndSortByDesc(ownerId,
                        PageRequest.of(0, 10, Sort.by("end").descending())))
                .thenReturn(Optional.of(bookingList));

        List<Booking> result = bookingService.getAllOwnerBookings(ownerId, state, from, size);

        Assertions.assertEquals(bookingList, result);
    }

    @Test
    @DisplayName("Получение прошлых бронирований владельца")
    void getAllOwnerBookingsPastState() {
        Long ownerId = 1L;
        String state = "PAST";
        Long from = 0L;
        Long size = 10L;

        User user = new User();
        List<Booking> bookingList = new ArrayList<>();

        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllPastByOwnerIdAndSortByDesc(ownerId, PageRequest.of(0, 10,
                        Sort.by("end").descending())))
                .thenReturn(Optional.of(bookingList));

        List<Booking> result = bookingService.getAllOwnerBookings(ownerId, state, from, size);

        Assertions.assertEquals(bookingList, result);
    }


    @Test
    @DisplayName("Получение бронирований владельца с неизвестным состоянием")
    void getAllOwnerBookingsUnknownState() {
        Long ownerId = 1L;
        String state = "UNKNOWN";
        Long from = 0L;
        Long size = 10L;

        User user = new User();

        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));

        Assertions.assertThrows(BadRequestException.class, () -> bookingService
                .getAllOwnerBookings(ownerId, state, from, size));
    }

    @Test
    @DisplayName("Получение бронирований владельца, когда пользователь не найден")
    void getAllOwnerBookingsUserNotFound() {
        Long ownerId = 1L;
        String state = "ALL";
        Long from = 0L;
        Long size = 10L;

        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> bookingService
                .getAllOwnerBookings(ownerId, state, from, size));
    }

    @Test
    @DisplayName("Получение бронирований владельца, когда список бронирований пуст")
    void getAllOwnerBookingsNoBookingsFound() {
        Long ownerId = 1L;
        String state = "ALL";
        Long from = 0L;
        Long size = 10L;

        User user = new User();

        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findBookingsByItemOwnerIdOrderByEndDesc(ownerId,
                        PageRequest.of(0, 10, Sort.by("end").descending())))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> bookingService
                .getAllOwnerBookings(ownerId, state, from, size));
    }

    @Test
    @DisplayName("Получение будущих бронирований владельца")
    void getAllOwnerBookingsFutureState() {
        Long ownerId = 1L;
        String state = "FUTURE";
        Long from = 0L;
        Long size = 10L;

        User user = new User();
        List<Booking> bookingList = new ArrayList<>();

        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllFutureByOwnerIdAndSortByDesc(ownerId, PageRequest.of(0, 10,
                Sort.by("end").descending()))).thenReturn(Optional.of(bookingList));

        List<Booking> result = bookingService.getAllOwnerBookings(ownerId, state, from, size);

        Assertions.assertEquals(bookingList, result);
    }

    @Test
    @DisplayName("Получение ожидающих бронирований владельца")
    void getAllOwnerBookingsWaitingState() {
        Long ownerId = 1L;
        String state = "WAITING";
        Long from = 0L;
        Long size = 10L;

        User user = new User();
        List<Booking> bookingList = new ArrayList<>();

        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findBookingsByStatusAndItemOwnerIdOrderByEndDesc(BookingStatus.WAITING, ownerId,
                        PageRequest.of(0, 10, Sort.by("end").descending())))
                .thenReturn(Optional.of(bookingList));

        List<Booking> result = bookingService.getAllOwnerBookings(ownerId, state, from, size);

        Assertions.assertEquals(bookingList, result);
    }

    @Test
    @DisplayName("Получение отклоненных бронирований владельца")
    void getAllOwnerBookingsRejectedState() {
        Long ownerId = 1L;
        String state = "REJECTED";
        Long from = 0L;
        Long size = 10L;

        User user = new User();
        List<Booking> bookingList = new ArrayList<>();

        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findBookingsByStatusAndItemOwnerIdOrderByEndDesc(BookingStatus.REJECTED,
                        ownerId, PageRequest.of(0, 10, Sort.by("end").descending())))
                .thenReturn(Optional.of(bookingList));

        List<Booking> result = bookingService.getAllOwnerBookings(ownerId, state, from, size);

        Assertions.assertEquals(bookingList, result);
    }
}