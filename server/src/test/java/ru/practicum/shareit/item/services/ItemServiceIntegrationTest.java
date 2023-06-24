package ru.practicum.shareit.item.services;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.InputCommentDto;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceIntegrationTest {

    private final ItemServiceImpl itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private User user;
    private User user2;
    private Item item;
    private Item item2;
    private Item item3;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("testUser");
        user.setEmail("test@email.ru");

        user2 = new User();
        user2.setName("testBooker");
        user2.setEmail("test@Bookeremail.ru");

        item = new Item(null, user, "testItem", "testDescription", true, null);
        item2 = new Item(null, user, "testItem2", "testDescription2", false,
                null);
        item3 = new Item(null, user, "testItem3", "testDescription3", false,
               null);

        booking = new Booking();
        booking.setBooker(user2);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusHours(24));
        booking.setEnd(LocalDateTime.now().minusHours(5));
        booking.setStatus(BookingStatus.APPROVED);
    }

    @DisplayName("Проверка получения всех предметов пользователем")
    @Test
    void getUserItemsTest() {
        userRepository.save(user);
        itemRepository.save(item);
        itemRepository.save(item2);
        itemRepository.save(item3);

        List<ItemDtoWithBooking> userItems = itemService.getUserItems(1);

        Assertions.assertAll("should return bookings from setUp",
                () -> Assertions.assertEquals(item.getDescription(), userItems.get(0).getDescription()),
                () -> Assertions.assertEquals(item2.getName(), userItems.get(1).getName()));
    }

    @DisplayName("Проверка получения определенного предмета пользователем")
    @Test
    void getUserItemTest() {
        ItemDtoWithBooking itemDtoWithBooking = new ItemDtoWithBooking(item.getName(), item.getDescription(),
                item.getAvailable());
        itemDtoWithBooking.setId(item.getId());
        itemDtoWithBooking.setComments(new ArrayList<>());
        userRepository.save(user);
        itemRepository.save(item);
        itemDtoWithBooking.setId(1L);
        Assertions.assertEquals(itemDtoWithBooking, itemService.getItem(1, 1));
    }

    @DisplayName("Тестирование поиска предмета c ожидаемым результатом")
    @Test
    void searchItemWithRegularResultTest() {
        userRepository.save(user);
        itemRepository.save(item);
        Assertions.assertEquals("testDescription", itemService.searchItem("it").get(0).getDescription());
    }

    @DisplayName("Проверка добавления предмета")
    @Test
    void addItemTest() {
        ItemDTO itemDTO = new ItemDTO(null, "Новый предмет", "Описание нового предмета",
                true, null);
        userRepository.save(user);
        ItemDTO addedItemDTO = itemService.addItem(user.getId(), itemDTO);
        assertNotNull(addedItemDTO.getId());
        assertEquals(itemDTO.getName(), addedItemDTO.getName());
        assertEquals(itemDTO.getDescription(), addedItemDTO.getDescription());
        assertEquals(1L, addedItemDTO.getId());
    }


    @DisplayName("Тестирование обновления предмета")
    @Test
    void updateItemTest() {
        ItemDTO itemDTO = new ItemDTO(1L, "updatedItem", "updatedDescription", true, null);
        userRepository.save(user);
        itemRepository.save(item);
        ItemDTO itemAfterUpdating = itemService.update(1L, itemDTO);
        Assertions.assertAll("должны вернуться имя и название из itemDTO",
                () -> Assertions.assertEquals(itemDTO.getName(), itemAfterUpdating.getName()),
                () -> Assertions.assertEquals(itemDTO.getDescription(), itemAfterUpdating.getDescription()));
    }

    @DisplayName("Тестирование удаления предмета")
    @Test
    void deleteItemTest() {
        userRepository.save(user);
        Assertions.assertNull(item.getId());
        itemRepository.save(item);
        assertEquals(1, (long) item.getId());
        itemService.deleteItem(1, 1);
        Assertions.assertFalse(itemRepository.findById(1L).isPresent());
    }

    @DisplayName("тестирование добавления комментария к предмету с booking")
    @Test
    void createCommentTest() {
        InputCommentDto inputCommentDto = new InputCommentDto("TestCommentString");
        Comment commentToSave = new Comment();
        commentToSave.setText(inputCommentDto.getText());

        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        bookingRepository.save(booking);

        Assertions.assertEquals(inputCommentDto.getText(), itemService.createComment(2L, 1L,
                inputCommentDto).getText());
    }
}