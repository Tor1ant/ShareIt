package ru.practicum.shareit.item;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputCommentDto;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.dto.ItemForRequestDto;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @InjectMocks
    private ItemController itemController;
    @Mock
    private ItemClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mvc;

    private static final String CUSTOM_USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        mvc = standaloneSetup(itemController).build();
        ItemForRequestDto itemForRequestDto = new ItemForRequestDto(null, "Тестовое название",
                "тестовое описание", true, 1L);
    }

    @DisplayName("Тестирование получения предметов")
    @Test
    void getUserItemsTest() throws Exception {
        ItemDtoWithBooking itemDtoWithBooking = new ItemDtoWithBooking("Тестовое дто", "тест",
                true);
        List<Object> list = List.of(itemDtoWithBooking);
        Mockito.when(client.getUserItems(anyLong()))
                .thenReturn(ResponseEntity.ok(list));

        mvc.perform(MockMvcRequestBuilders.get("/items")
                        .header(CUSTOM_USER_ID_HEADER, 2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Тестовое дто"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("тест"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available").value(true));
    }

    @DisplayName("Тестирование получения одного предмета")
    @Test
    void getItemTest() throws Exception {
        ItemDtoWithBooking itemDtoWithBooking = new ItemDtoWithBooking("Тестовое дто", "тест",
                true);
        Mockito.when(client.getItem(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(itemDtoWithBooking));

        mvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .header(CUSTOM_USER_ID_HEADER, 2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Тестовое дто"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("тест"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(true));
    }

    @DisplayName("Тестирование добавления предмета")
    @Test
    void addTest() throws Exception {
        ItemDTO itemDTO = new ItemDTO(null, "Тестовое название", "Тестовое описание", true,
                null);
        Mockito.when(client.addItem(anyLong(), any(ItemDTO.class)))
                .thenReturn(ResponseEntity.ok(itemDTO));

        mvc.perform(post("/items", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDTO))
                        .header(CUSTOM_USER_ID_HEADER, 2)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Тестовое название"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Тестовое описание"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(true));
    }

    @DisplayName("Тестирование обновления предмета")
    @Test
    void update() throws Exception {
        ItemDTO itemDTO = new ItemDTO(null, "Обновленное название", "Обновленное описание",
                true, null);

        Mockito.when(client.update(anyLong(), any(), anyLong()))
                .thenReturn(ResponseEntity.ok(itemDTO));

        mvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", 1L)
                        .header(CUSTOM_USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                        .value("Обновленное название"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value("Обновленное описание"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(true));
    }

    @DisplayName("Проверка удаления предмета")
    @Test
    void deleteItem() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/items/{itemId}", 1L)
                        .header(CUSTOM_USER_ID_HEADER, 1L))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(client, Mockito.times(1)).deleteItem(anyLong(),
                anyLong());
    }

    @DisplayName("Проверка поиска предмета")
    @Test
    void searchItem() throws Exception {
        String searchText = "test";

        List<ItemDTO> itemList = Arrays.asList(
                new ItemDTO(1L, "Item 1", "Description 1", true, null),
                new ItemDTO(2L, "Item 2", "Description 2", true, null)
        );

        Mockito.when(client.searchItem(any(String.class), anyLong())).thenReturn(ResponseEntity.ok(itemList));

        mvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .header(CUSTOM_USER_ID_HEADER, 1L)
                        .param("text", searchText))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("Item 1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", Matchers.is("Description 1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", Matchers.is("Item 2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description", Matchers.is("Description 2")));
        Mockito.verify(client, Mockito.times(1)).searchItem(any(String.class), anyLong());
    }

    @DisplayName("Проверка создания комментария")
    @Test
    void createComment() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        String commentText = "Test comment";

        CommentDto commentDto = new CommentDto(1L, commentText, "Тестовый автор");

        InputCommentDto inputCommentDto = new InputCommentDto();
        inputCommentDto.setText(commentText);

        Mockito.when(client.createComment(userId, itemId, inputCommentDto)).thenReturn(ResponseEntity.ok(commentDto));

        mvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", itemId)
                        .header(CUSTOM_USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentText)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorName", Matchers.is(commentDto.getAuthorName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.text", Matchers.is(commentText)));

        Mockito.verify(client, Mockito.times(1)).createComment(userId, itemId, inputCommentDto);
    }
}