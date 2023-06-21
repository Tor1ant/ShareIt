package ru.practicum.shareit.request;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestData;
import ru.practicum.shareit.user.model.User;

@RestClientTest(ItemRequestClient.class)
class ItemRequestClientTest {

    @Autowired
    private ItemRequestClient client;
    @Autowired
    private MockRestServiceServer mockRestServiceServer;
    @Autowired
    private ObjectMapper objectMapper;

    private String request;
    private String result;
    private ItemRequestInputDto itemRequestInputDto;

    @BeforeEach
    void setUp() throws JsonProcessingException {

        User user = new User();
        user.setName("testUser");
        user.setEmail("test@email.ru");
        user.setId(1L);

        User user2 = new User();
        user2.setName("testUser2");
        user2.setEmail("test@email.ru2");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(user);
        itemRequest.setDescription("тестовое описание запрашиваемого предмета");
        itemRequest.setId(1L);
        itemRequest.setCreated(LocalDateTime.now());

        itemRequestInputDto = new ItemRequestInputDto("Тестовый предмет",
                LocalDateTime.now());
        ItemRequestData itemRequestData = new ItemRequestData(itemRequestInputDto, user.getId());
        request = objectMapper.writeValueAsString(itemRequestInputDto);
        result = objectMapper.writeValueAsString(itemRequest);

    }

    @Test
    void postItemRequest() {
        this.mockRestServiceServer.expect(requestTo("http://localhost:9090/requests"))
                .andExpect(content().json(request))
                .andRespond(withSuccess(result, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = this.client.postItemRequest(itemRequestInputDto, 1L);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getItemRequests() {
        this.mockRestServiceServer.expect(requestTo("http://localhost:9090/requests"))
                .andExpect(anything())
                .andRespond(withSuccess(result, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = this.client.getItemRequests(1L);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getItemRequestsInPages() {
        this.mockRestServiceServer.expect(requestTo("http://localhost:9090/requests/all?from=0&size=1"))
                .andExpect(anything())
                .andRespond(withSuccess(result, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = this.client.getItemRequestsInPages(1L, 0L, 1L);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getItemRequest() {
        this.mockRestServiceServer.expect(requestTo("http://localhost:9090/requests/1"))
                .andExpect(anything())
                .andRespond(withSuccess(result, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = this.client.getItemRequest(1L, 1L);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}