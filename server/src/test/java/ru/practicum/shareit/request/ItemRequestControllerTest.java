package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.ItemController.CUSTOM_USER_ID_HEADER;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    @InjectMocks
    private ItemRequestController itemRequestController;
    @Mock
    private ItemRequestServiceImpl itemRequestService;
    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ItemRequest itemRequest;
    private ItemRequestInputDto inputDto;
    private ItemRequestDtoWithItems requestDtoWithItems;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();
        User user = new User();
        user.setName("testUser");
        user.setEmail("test@email.ru");
        user.setId(1L);

        inputDto = new ItemRequestInputDto("Тестовый предмет",
                LocalDateTime.now());
        itemRequest = new ItemRequest();
        itemRequest.setRequester(user);
        itemRequest.setDescription("Тестовая заявка на предмет");
        itemRequest.setCreated(LocalDateTime.now());

        requestDtoWithItems = new ItemRequestDtoWithItems();
        requestDtoWithItems.setDescription("Список запрошенных предметов");
    }

    @DisplayName("Проверка создания запроса на предмет")
    @Test
    void postItemRequestTest() throws Exception {
        Mockito.when(itemRequestService.postItemRequest(Mockito.any())).thenReturn(itemRequest);
        mvc.perform(post("/requests")
                        .header(CUSTOM_USER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(inputDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription()), String.class));
    }

    @DisplayName("Проверка получения запросов на предметы владельцем")
    @Test
    void getItemsRequest() throws Exception {
        Mockito.when(itemRequestService.getItemRequests(Mockito.any())).thenReturn(List.of(requestDtoWithItems));
        mvc.perform(get("/requests")
                        .header(CUSTOM_USER_ID_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(requestDtoWithItems.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(requestDtoWithItems.getDescription()), String.class));
    }

    @DisplayName("Проверка получения запроса на предметы не владельцем")
    @Test
    void getItemsRequestInPagesTest() throws Exception {
        Mockito.when(itemRequestService.getItemRequestsInPages(Mockito.any(), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(List.of(requestDtoWithItems));

        mvc.perform(get("/requests/all")
                        .header(CUSTOM_USER_ID_HEADER, 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(requestDtoWithItems.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(requestDtoWithItems.getDescription()),
                        String.class));
    }

    @DisplayName("Проверка получения запроса на предметы с началом выдачи меньше 0")
    @Test
    void getItemsRequestInPagesLessThen0Test() {
        Assertions.assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/requests/all?from=-5&size=5")
                                .header(CUSTOM_USER_ID_HEADER, 2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.detailMessage", is("Индекс первого элемента не может быть" +
                                " меньше нуля"))));
    }

    @DisplayName("Проверка получения запроса на предметы с размером выдачи меньше 0")
    @Test
    void getItemsRequestInPagesSizeLessThen0Test() {
        Assertions.assertThrows(NestedServletException.class,
                () -> mvc.perform(get("/requests/all?from=0&size=-5")
                                .header(CUSTOM_USER_ID_HEADER, 2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.detailMessage", is("Количество элементов для отображения" +
                                " не может быть меньше или равно нулю"))));
    }

    @DisplayName("Проверка получения одного запроса на предмет")
    @Test
    void getItemRequestTest() throws Exception {
        Mockito.when(itemRequestService.getItemRequest(Mockito.any(), Mockito.anyLong()))
                .thenReturn(requestDtoWithItems);

        mvc.perform(get("/requests/1")
                        .header(CUSTOM_USER_ID_HEADER, 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(requestDtoWithItems.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoWithItems.getDescription()),
                        String.class));
    }
}
