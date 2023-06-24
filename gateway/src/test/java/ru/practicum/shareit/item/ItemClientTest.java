package ru.practicum.shareit.item;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.InputCommentDto;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@RestClientTest(ItemClient.class)
class ItemClientTest {

    @Autowired
    private ItemClient client;
    @Autowired
    private MockRestServiceServer mockRestServiceServer;
    @Autowired
    private ObjectMapper objectMapper;
    private Item item;
    private Item item2;
    private Item item3;
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

        Booking booking2 = new Booking();
        Booking booking3 = new Booking();

        booking2.setId(2L);
        booking2.setItem(item2);
        booking2.setStart(LocalDateTime.now());
        booking2.setEnd(booking2.getStart().plusHours(10));

        booking3.setId(3L);
        booking3.setItem(item3);
        booking3.setStart(LocalDateTime.now());
        booking3.setEnd(booking3.getStart().plusHours(10));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getUserItems() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(item);
        mockRestServiceServer.expect(requestTo("http://localhost:9090/items"))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> userItems = this.client.getUserItems(1L);
        Assertions.assertNotNull(userItems);
        Assertions.assertEquals(HttpStatus.OK, userItems.getStatusCode());
    }

    @Test
    void getItem() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(item);
        mockRestServiceServer.expect(requestTo("http://localhost:9090/items/1"))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> itemResponse = this.client.getItem(1L, 1L);
        Assertions.assertNotNull(itemResponse);
        Assertions.assertEquals(HttpStatus.OK, itemResponse.getStatusCode());
    }

    @Test
    void addItem() throws JsonProcessingException {
        ItemDTO itemDTO = new ItemDTO(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getRequest().getId());
        String request = objectMapper.writeValueAsString(itemDTO);
        mockRestServiceServer.expect(requestTo("http://localhost:9090/items"))
                .andExpect(content().json(request))
                .andRespond(withSuccess());

        ResponseEntity<Object> addItemResponse = this.client.addItem(1L, itemDTO);
        Assertions.assertNotNull(addItemResponse);
        Assertions.assertEquals(HttpStatus.OK, addItemResponse.getStatusCode());
    }

    @Test
    void update() throws JsonProcessingException {
        ItemDTO itemDTO = new ItemDTO(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getRequest().getId());
        String request = objectMapper.writeValueAsString(itemDTO);
        mockRestServiceServer.expect(requestTo("http://localhost:9090/items/1"))
                .andExpect(content().json(request))
                .andRespond(withSuccess());

        ResponseEntity<Object> updateResponse = this.client.update(1L, itemDTO, 1L);
        Assertions.assertNotNull(updateResponse);
        Assertions.assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
    }


    @Test
    void deleteItem() {
        mockRestServiceServer.expect(requestTo("http://localhost:9090/items/1"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.DELETE))
                .andRespond(withSuccess());
        this.client.deleteItem(1L, 1L);
    }


    @Test
    void searchItem() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(Arrays.asList(item, item2, item3));
        mockRestServiceServer.expect(requestTo("http://localhost:9090/items/search?text=test"))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> searchResponse = this.client.searchItem("test", 1L);
        Assertions.assertNotNull(searchResponse);
        Assertions.assertEquals(HttpStatus.OK, searchResponse.getStatusCode());
    }

    @Test
    void createComment() throws JsonProcessingException {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setText("Test comment");
        InputCommentDto inputCommentDto = new InputCommentDto(comment.getText());
        String request = objectMapper.writeValueAsString(inputCommentDto);
        mockRestServiceServer.expect(requestTo("http://localhost:9090/items/1/comment"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andExpect(content().json(request))
                .andRespond(withSuccess());

        ResponseEntity<Object> createCommentResponse = this.client.createComment(1L, 1L,
                new InputCommentDto("Test comment"));
        Assertions.assertNotNull(createCommentResponse);
        Assertions.assertEquals(HttpStatus.OK, createCommentResponse.getStatusCode());
    }
}