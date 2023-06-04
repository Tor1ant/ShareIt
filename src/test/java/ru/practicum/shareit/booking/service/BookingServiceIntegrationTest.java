package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@SpringBootTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
class BookingServiceIntegrationTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingService bookingService;

    private Item item;
    private Item item2;
    private User user;
    private User user2;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("testUser");
        user.setEmail("test@email.ru");

        user2 = new User();
        user2.setName("testUserSecond");
        user2.setEmail("testSecond@email.ru");

        item = new Item(null, user, "testItem", "testDescription", true, null);
        item2 = new Item(null, user, "testItem2", "testDescription2", true, null);
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
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);

        Assertions.assertEquals(item, bookingService.createBooking(bookingInputDto, 2L).getItem());
    }

    @Test
    void getBooking() {
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(1L)
                .status(BookingStatus.APPROVED)
                .build();
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        bookingService.createBooking(bookingInputDto, 2L);
        Assertions.assertEquals(item, bookingService.getBooking(2L, 1L).getItem());
    }

    @Test
    @DisplayName("Проверка установки статуса одобрения бронирования")
    void setApprovedTest() {
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(1L)
                .status(BookingStatus.WAITING)
                .build();
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        bookingService.createBooking(bookingInputDto, 2L);
        bookingService.setApproved(user.getId(), 1L, true);
        Assertions.assertEquals(BookingStatus.APPROVED, bookingService.getBooking(2L, 1L).getStatus());
    }

    @DisplayName("Получение всех бронирований")
    @Test
    void getAllBookingsTest() {
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(1L)
                .status(BookingStatus.WAITING)
                .build();
        BookingInputDto bookingInputDto2 = BookingInputDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(2L)
                .status(BookingStatus.WAITING)
                .build();
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);
        bookingService.createBooking(bookingInputDto, 2L);
        bookingService.setApproved(user.getId(), 1L, true);
        bookingService.createBooking(bookingInputDto2, 2L);
        bookingService.setApproved(user.getId(), 2L, true);

        List<Booking> allBookings = bookingService.getAllBookings(2L, "ALL", 0L, 4L);
        allBookings.sort((b, b2) -> Math.toIntExact(b.getId() - b2.getId()));
        Assertions.assertAll("должны вернуться нужные предметы в бронированиях",
                () -> Assertions.assertEquals(item, allBookings.get(0).getItem()),
                () -> Assertions.assertEquals(item2, allBookings.get(1).getItem()));
    }

    @DisplayName("Просмотр бронирований владельцем бронируемого предмета")
    @Test
    void getAllOwnerBookingsTest() {
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(1L)
                .status(BookingStatus.WAITING)
                .build();
        BookingInputDto bookingInputDto2 = BookingInputDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(2L)
                .status(BookingStatus.WAITING)
                .build();
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);
        bookingService.createBooking(bookingInputDto, 2L);
        bookingService.setApproved(user.getId(), 1L, true);
        bookingService.createBooking(bookingInputDto2, 2L);
        bookingService.setApproved(user.getId(), 2L, true);

        List<Booking> allBookings = bookingService.getAllOwnerBookings(1L, "ALL", 0L, 4L);
        allBookings.sort((b, b2) -> Math.toIntExact(b.getId() - b2.getId()));
        Assertions.assertAll("должны вернуться нужные предметы в бронированиях",
                () -> Assertions.assertEquals(item, allBookings.get(0).getItem()),
                () -> Assertions.assertEquals(item2, allBookings.get(1).getItem()));
    }
}