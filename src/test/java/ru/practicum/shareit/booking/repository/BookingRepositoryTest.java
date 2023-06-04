package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
})
@ExtendWith(SpringExtension.class)
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;

    private Item item;
    private Item item2;
    private User user;
    private User user2;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("testUser");
        user.setEmail("test@email.ru");
        user2 = new User();
        user2.setName("TestBooker");
        user2.setEmail("testBooker@email.ru");

        item = new Item(null, user, "testItem", "testDescription", true,
                null);
        item2 = new Item(null, user, "testItem3", "testDescription3", true,
                null);

        booking = new Booking();
        booking.setBooker(user2);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item2);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(25));
    }

    @DisplayName("Проверка получения бронирования из базы данных")
    @Test
    void findByIdWithItemAndBooker() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);

        Optional<Booking> optionalBooking = bookingRepository.findByIdWithItemAndBooker(booking.getId(), user2.getId());
        Assertions.assertTrue(optionalBooking.isPresent());
        Assertions.assertEquals(booking, optionalBooking.get());
    }

    @DisplayName("Проверка получения всех текущих бронирований пользователя")
    @Test
    void findAllCurrentByUserIdAndSortByDesc() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        long size = 4L;
        PageRequest sortedByEndDesc = PageRequest.of(0, (int) size, Sort.by("end").descending());

        Optional<List<Booking>> optionalBooking = bookingRepository
                .findAllCurrentByUserIdAndSortByDesc(user2.getId(), sortedByEndDesc);
        Assertions.assertTrue(optionalBooking.isPresent());
        Assertions.assertEquals(booking, optionalBooking.get().get(0));
    }

    @DisplayName("Проверка получения всех текущих бронирований Владельцем")
    @Test
    void findAllCurrentByOwnerIdAndSortByDesc() {
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        long size = 4L;
        PageRequest sortedByEndDesc = PageRequest.of(0, (int) size, Sort.by("end").descending());

        Optional<List<Booking>> optionalBooking = bookingRepository
                .findAllCurrentByOwnerIdAndSortByDesc(user.getId(), sortedByEndDesc);
        Assertions.assertTrue(optionalBooking.isPresent());
        Assertions.assertEquals(booking, optionalBooking.get().get(0));
    }

    @DisplayName("Проверка получения всех прошедших бронирований Пользователем")
    @Test
    void findAllPastByUserIdAndSortByDesc() {
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(booking.getStart().plusHours(10));
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        long size = 4L;
        PageRequest sortedByEndDesc = PageRequest.of(0, (int) size, Sort.by("end").descending());

        Optional<List<Booking>> optionalBooking = bookingRepository
                .findAllPastByUserIdAndSortByDesc(user2.getId(), sortedByEndDesc);
        Assertions.assertTrue(optionalBooking.isPresent());
        Assertions.assertEquals(booking, optionalBooking.get().get(0));
    }

    @DisplayName("Проверка получения всех прошедших бронирований Владельцем")
    @Test
    void findAllPastByOwnerIdAndSortByDesc() {
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(booking.getStart().plusHours(10));
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        long size = 4L;
        PageRequest sortedByEndDesc = PageRequest.of(0, (int) size, Sort.by("end").descending());

        Optional<List<Booking>> optionalBooking = bookingRepository
                .findAllPastByOwnerIdAndSortByDesc(user.getId(), sortedByEndDesc);
        Assertions.assertTrue(optionalBooking.isPresent());
        Assertions.assertEquals(booking, optionalBooking.get().get(0));
    }

    @Test
    void findAllFutureByUserIdAndSortByDesc() {
        booking.setStart(LocalDateTime.now().plusDays(25));
        booking.setEnd(LocalDateTime.now().plusDays(30));
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        long size = 4L;
        PageRequest sortedByEndDesc = PageRequest.of(0, (int) size, Sort.by("end").descending());

        Optional<List<Booking>> optionalBooking = bookingRepository
                .findAllFutureByUserIdAndSortByDesc(user2.getId(), sortedByEndDesc);
        Assertions.assertTrue(optionalBooking.isPresent());
        Assertions.assertEquals(booking, optionalBooking.get().get(0));
    }

    @Test
    void findAllFutureByOwnerIdAndSortByDesc() {
        booking.setStart(LocalDateTime.now().plusDays(25));
        booking.setEnd(LocalDateTime.now().plusDays(30));
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        long size = 4L;
        PageRequest sortedByEndDesc = PageRequest.of(0, (int) size, Sort.by("end").descending());

        Optional<List<Booking>> optionalBooking = bookingRepository
                .findAllFutureByOwnerIdAndSortByDesc(user.getId(), sortedByEndDesc);
        Assertions.assertTrue(optionalBooking.isPresent());
        Assertions.assertEquals(booking, optionalBooking.get().get(0));
    }

    @Test
    void findBookingsByItemId() {
        em.persist(user);
        em.persist(user2);
        em.persist(item2);
        em.persist(booking);

        Optional<List<Booking>> optionalBooking = Optional.ofNullable(bookingRepository
                .findBookingsByItemId(List.of(item2.getId())));
        Assertions.assertTrue(optionalBooking.isPresent());
        Assertions.assertEquals(booking, optionalBooking.get().get(0));
    }

    @Test
    void findBookingByBookerIdAndItemIdAndStatusApproved() {
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(LocalDateTime.now().minusDays(4));
        booking.setStatus(BookingStatus.APPROVED);

        em.persist(user);
        em.persist(user2);
        em.persist(item2);
        em.persist(booking);

        Optional<List<Booking>> optionalBooking = Optional.ofNullable(bookingRepository
                .findBookingByBookerIdAndItemIdAndStatusApproved(user2.getId(), item2.getId()));
        Assertions.assertTrue(optionalBooking.isPresent());
        Assertions.assertEquals(booking, optionalBooking.get().get(0));
    }
}