package ru.practicum.shareit.item.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingIdAndBookerDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.InputCommentDto;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RequestRepository requestRepository;

    private Item item;
    private Item item2;
    private Item item3;

    private Booking booking;
    private Booking booking2;
    private Booking booking3;
    private User user;

    @BeforeEach
    void setUp() {
        item = new Item(1L, new User(), "testItem", "testDescription", true,
                new ItemRequest());
        item2 = new Item(2L, new User(), "testItem2", "testDescription2", false,
                new ItemRequest());
        item3 = new Item(3L, new User(), "testItem3", "testDescription3", false,
                new ItemRequest());

        user = new User();
        user.setName("testUser");
        user.setEmail("test@email.ru");

        booking = new Booking();
        booking2 = new Booking();
        booking3 = new Booking();

        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(5));

        booking2.setId(2L);
        booking2.setItem(item2);
        booking2.setStart(LocalDateTime.now());
        booking2.setEnd(booking2.getStart().plusHours(10));

        booking3.setId(3L);
        booking3.setItem(item3);
        booking3.setStart(LocalDateTime.now());
        booking3.setEnd(booking3.getStart().plusHours(10));
    }

    @DisplayName("Проверка получения всех предметов пользователем")
    @Test
    void getUserItemsTest() {

        Mockito.when(itemRepository.findItemsByOwnerIdOrderByIdAsc(Mockito.anyLong())).thenReturn(List.of(item,
                item2, item3));
        Mockito.when(bookingRepository.findBookingsByItemId(Mockito.anyList())).thenReturn(List.of(booking, booking2,
                booking3));

        List<ItemDtoWithBooking> userItems = itemService.getUserItems(1);

        Assertions.assertAll("should return bookings from setUp",
                () -> Assertions.assertEquals(booking.getId(), userItems.get(0).getLastBooking().getId()),
                () -> Assertions.assertEquals(booking2.getId(), userItems.get(1).getLastBooking().getId()),
                () -> Assertions.assertEquals(booking3.getId(), userItems.get(2).getLastBooking().getId()));


        Mockito.verify(itemRepository, Mockito.times(1)).findItemsByOwnerIdOrderByIdAsc(Mockito
                .anyLong());
        Mockito.verify(bookingRepository, Mockito.times(1)).findBookingsByItemId(Mockito
                .anyList());
    }

    @DisplayName("Проверка получения несуществующего предмета")
    @Test
    void getItemNotFoundTest() {
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.getItem(1, 1));
        Mockito.verify(itemRepository, Mockito.times(1)).findById(1L);
    }

    @DisplayName("Проверка получения определенного предмета пользователем")
    @Test
    void getUserItemTest() {
        ItemDtoWithBooking itemDtoWithBooking = new ItemDtoWithBooking(item.getName(), item.getDescription(),
                item.getAvailable());
        itemDtoWithBooking.setId(item.getId());
        itemDtoWithBooking.setComments(new ArrayList<>());
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> Optional.of(item));

        Assertions.assertEquals(itemDtoWithBooking, itemService.getItem(1, 1));

        Mockito.verify(itemRepository, Mockito.times(1)).findById(Mockito
                .anyLong());
    }

    @DisplayName("Проверка получения определенного предмета пользователем с следующим бронированием")
    @Test
    void getUserItemWithNextBookingTest() {
        ItemDtoWithBooking itemDtoWithBooking = new ItemDtoWithBooking(item.getName(), item.getDescription(),
                item.getAvailable());
        BookingIdAndBookerDto bookingIdAndBookerDto = new BookingIdAndBookerDto();
        itemDtoWithBooking.setId(item.getId());
        itemDtoWithBooking.setComments(new ArrayList<>());
        bookingIdAndBookerDto.setId(booking.getId());
        itemDtoWithBooking.setLastBooking(bookingIdAndBookerDto);

        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> Optional.of(item));
        Mockito.when(itemRepository.getItemByIdAndOwnerId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findBookingsByItemIdOrderByEndDesc(Mockito.anyLong()))
                .thenReturn(Optional.of(List.of(booking)));

        Assertions.assertEquals(itemDtoWithBooking, itemService.getItem(1, 1));

        Mockito.verify(itemRepository, Mockito.times(1)).findById(Mockito
                .anyLong());
        Mockito.verify(bookingRepository, Mockito.times(1)).findBookingsByItemIdOrderByEndDesc(
                Mockito.anyLong());
        Mockito.verify(itemRepository, Mockito.times(1)).getItemByIdAndOwnerId(Mockito.anyLong(),
                Mockito.anyLong());
    }


    @DisplayName("Проверка получения определенного предмета владельца")
    @Test
    void getOwnerItemTest() {
        ItemDtoWithBooking itemDtoWithBooking = new ItemDtoWithBooking(item.getName(), item.getDescription(),
                item.getAvailable());
        BookingIdAndBookerDto bookingIdAndBookerDto = new BookingIdAndBookerDto();
        itemDtoWithBooking.setId(item.getId());
        itemDtoWithBooking.setComments(new ArrayList<>());
        bookingIdAndBookerDto.setId(booking.getId());
        itemDtoWithBooking.setLastBooking(bookingIdAndBookerDto);

        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> Optional.of(item));
        Mockito.when(itemRepository.getItemByIdAndOwnerId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findBookingsByItemIdOrderByEndDesc(Mockito.anyLong()))
                .thenReturn(Optional.of(List.of(booking)));

        Assertions.assertEquals(itemDtoWithBooking, itemService.getItem(1, 1));

        Mockito.verify(itemRepository, Mockito.times(1)).findById(Mockito
                .anyLong());
        Mockito.verify(bookingRepository, Mockito.times(1)).findBookingsByItemIdOrderByEndDesc(
                Mockito.anyLong());
        Mockito.verify(itemRepository, Mockito.times(1)).getItemByIdAndOwnerId(Mockito.anyLong(),
                Mockito.anyLong());
    }

    @DisplayName("Тестирование поиска с пустой строкой вместо запроса")
    @Test
    void searchItemWithEmptyStringTest() {
        Assertions.assertEquals(new ArrayList<>(), itemService.searchItem(""));
    }

    @DisplayName("Тестирование поиска предмета, который выбросит NotFoundEx")
    @Test
    void searchItemNotFoundTest() {
        Mockito.when(itemRepository.search(Mockito.anyString())).thenAnswer(invocationOnMock -> Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.searchItem("тестовый запрос"));
        Mockito.verify(itemRepository, Mockito.times(1)).search(Mockito.anyString());
    }

    @DisplayName("Тестирование поиска предмета c ожидаемым результатом")
    @Test
    void searchItemWithRegularResultTest() {
        Mockito.when(itemRepository.search(Mockito.anyString())).thenReturn(Optional.of(List.of(item, item2, item3)));

        Assertions.assertAll("ожидаются нужные id предметов",
                () -> Assertions.assertEquals(item.getId(), itemService.searchItem("testString").get(0).getId()),
                () -> Assertions.assertEquals(item2.getId(), itemService.searchItem("testString").get(1).getId()),
                () -> Assertions.assertEquals(item3.getId(), itemService.searchItem("testString").get(2).getId()));
        Mockito.verify(itemRepository, Mockito.times(3)).search(Mockito.anyString());
    }

    @DisplayName("Тестирование добавления предмета несуществующим пользователем")
    @Test
    void addItemWithoutUserTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenAnswer(invocationOnMock -> Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.addItem(1L, null));
    }

    @DisplayName("Тестирование добавления предмета по запросу другого пользователя")
    @Test
    void addItemByRequestAnotherUserTest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("Тестовый предмет для тестов");
        itemRequest.setId(1L);

        ItemDTO itemDTO = new ItemDTO(null, "testItem", "testDescription", true, 1L);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(requestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);

        Assertions.assertEquals(1L, itemService.addItem(1L, itemDTO).getRequestId());
    }

    @DisplayName("Тестирование добавления предмета")
    @Test
    void addItemTest() {
        ItemDTO itemDTO = new ItemDTO(1L, "testItem", "testDescription", true, null);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.save(Mockito.any(Item.class))).thenReturn(item);
        Assertions.assertEquals(item.getId(), itemService.addItem(1L, itemDTO).getId());

    }

    @DisplayName("Тестирование обновления предмета несуществующим пользователем")
    @Test
    void updateWhenUserNotFound() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenAnswer(invocationOnMock -> Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.update(1L, null));

    }

    @DisplayName("Тестирование обновления предмета")
    @Test
    void updateItemTest() {
        ItemDTO itemDTO = new ItemDTO(1L, "updatedItem", "updatedDescription", true, null);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findItemByIdAndOwnerId(Mockito.any(), Mockito.anyLong())).thenReturn(item);
        ItemDTO itemAfterUpdating = itemService.update(1L, itemDTO);
        Assertions.assertAll("должны вернуться имя и название из itemDTO",
                () -> Assertions.assertEquals(itemDTO.getName(), itemAfterUpdating.getName()),
                () -> Assertions.assertEquals(itemDTO.getDescription(), itemAfterUpdating.getDescription()));
    }

    @DisplayName("Тестирование удаления предмета")
    @Test
    void deleteItemTest() {
        itemService.deleteItem(1, 1);
        Mockito.verify(itemRepository, Mockito.times(1)).deleteByIdAndOwnerId(Mockito.anyLong(),
                Mockito.anyLong());
    }

    @DisplayName("неудачное добавление комментария к предмету без booking")
    @Test
    void createCommentWhenBookingNotExistTest() {
        Mockito.when(bookingRepository.findBookingByBookerIdAndItemIdAndStatusApproved(Mockito.anyLong(),
                        Mockito.anyLong()))
                .thenReturn(new ArrayList<>());
        Assertions.assertThrows(BadRequestException.class,
                () -> itemService.createComment(1L, 1L, new InputCommentDto()));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findBookingByBookerIdAndItemIdAndStatusApproved(Mockito.anyLong(), Mockito.anyLong());
    }

    @DisplayName("тестирование добавления комментария к предмету с booking")
    @Test
    void createCommentTest() {
        InputCommentDto inputCommentDto = new InputCommentDto("TestCommentString");
        Comment commentToSave = new Comment();
        commentToSave.setText(inputCommentDto.getText());
        Mockito.when(bookingRepository.findBookingByBookerIdAndItemIdAndStatusApproved(Mockito.anyLong(),
                        Mockito.anyLong()))
                .thenReturn(List.of(booking));
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(commentToSave);
        Assertions.assertEquals(inputCommentDto.getText(), itemService.createComment(1L, 1L, inputCommentDto)
                .getText());
    }
}