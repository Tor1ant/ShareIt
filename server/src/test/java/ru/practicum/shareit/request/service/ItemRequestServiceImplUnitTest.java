package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestData;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplUnitTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    private User user;
    private ItemRequestData itemRequestData;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("testUser");
        user.setEmail("test@email.ru");
        user.setId(1L);
        ItemRequestInputDto itemRequestInputDto = new ItemRequestInputDto("Тестовый предмет",
                LocalDateTime.now());
        itemRequestData = new ItemRequestData(itemRequestInputDto, user.getId());
    }


    @DisplayName("Тестирование создания запроса предмета несуществующим пользователем")
    @Test
    void postItemRequestNotFoundUserTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenThrow(NotFoundException.class);
        Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.postItemRequest(new ItemRequestData(null, 1L)));
    }

    @DisplayName("Тестирование создания запроса предмета  пользователем")
    @Test
    void postItemRequestTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(requestRepository.save(Mockito.any())).thenReturn(Mockito.any());
        Assertions.assertEquals("Тестовый предмет", itemRequestService.postItemRequest(itemRequestData)
                .getDescription());
        Mockito.verify(requestRepository, Mockito.times(1)).save(Mockito.any());
    }

    @DisplayName("Проверка получения запросов предметов не владельцем")
    @Test
    void getItemRequestsTest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(user);
        itemRequest.setDescription("тестовое описание запрашиваемого предмета");
        itemRequest.setId(1L);
        itemRequest.setCreated(LocalDateTime.now());

        ItemForRequestDto item = new ItemForRequestDto(1L, "предмет дя запроса",
                "тестовое описание", true, 1L);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(requestRepository.countAllByRequesterId(Mockito.anyLong())).thenReturn(1L);
        Mockito.when(requestRepository.findAllByRequesterIdOrderByCreatedAsc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(itemRequest));
        Mockito.when(itemRepository.findAllByRequests(Mockito.any())).thenReturn(List.of(item));

        Assertions.assertEquals("тестовое описание запрашиваемого предмета",
                itemRequestService.getItemRequests(1L).get(0).getDescription());

    }

    @DisplayName("Проверка получения запросов предметов владельцем запросов")
    @Test
    void getItemRequestsInPagesTest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(user);
        itemRequest.setDescription("тестовое описание запрашиваемого предмета");
        itemRequest.setId(1L);
        itemRequest.setCreated(LocalDateTime.now());

        ItemForRequestDto item = new ItemForRequestDto(1L, "предмет дя запроса",
                "тестовое описание", true, 1L);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(requestRepository.findAllByRequesterIdNot(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(itemRequest));
        Mockito.when(itemRepository.findAllByRequests(Mockito.any())).thenReturn(List.of(item));

        Assertions.assertEquals("тестовое описание запрашиваемого предмета",
                itemRequestService.getItemRequestsInPages(1L, 0L, 4L).get(0).getDescription());
    }

    @DisplayName("Проверка получения одного запроса предмета несуществующим пользователем")
    @Test
    void getItemRequestWithoutUserTest() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);
        Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequest(1L, 1L));
        Mockito.verify(userRepository, Mockito.times(1)).existsById(Mockito.anyLong());
    }

    @DisplayName("Проверка получения несуществующего запроса предмета пользователем")
    @Test
    void getItemRequestWithoutRequestTest() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequest(1L, 1L));
        Mockito.verify(userRepository, Mockito.times(1)).existsById(Mockito.anyLong());
        Mockito.verify(requestRepository, Mockito.times(1)).existsById(Mockito.anyLong());
    }

    @DisplayName("Проверка получения одного запроса предмета пользователем")
    @Test
    void getItemRequestTest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(user);
        itemRequest.setDescription("тестовое описание запрашиваемого предмета");
        itemRequest.setId(1L);
        itemRequest.setCreated(LocalDateTime.now());

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(requestRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(requestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(itemRequest));

        Assertions.assertEquals("тестовое описание запрашиваемого предмета",
                itemRequestService.getItemRequest(1L, 1L).getDescription());
    }
}