package ru.practicum.shareit.user;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.user.model.User;

@RestClientTest(UserClient.class)
class UserClientTest {

    @Autowired
    private UserClient client;
    @Autowired
    private MockRestServiceServer mockRestServiceServer;
    @Autowired
    private ObjectMapper objectMapper;
    private User user;
    private String request;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        user = new User();
        user.setName("testUser");
        user.setEmail("test@email.ru");
        User user2 = new User();
        user2.setName("testUser2");
        user2.setEmail("test@email.ru2");

        request = objectMapper.writeValueAsString(user);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @SuppressWarnings("unchecked")
    void getUser() {
        this.mockRestServiceServer.expect(requestTo("http://localhost:9090/users/1"))
                .andRespond(withSuccess(request, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> objectResponseEntity = this.client.getUser(1);
        LinkedHashMap<String, String> userRes = (LinkedHashMap<String, String>) objectResponseEntity.getBody();
        assert userRes != null;
        Assertions.assertNull(userRes.get("id"));
        Assertions.assertEquals(user.getName(), userRes.get("name"));
        Assertions.assertEquals(user.getEmail(), userRes.get("email"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAllUsers() {
        String response = "[{\"id\": 1, \"name\": \"user1\", \"email\": \"user1@example.com\"},"
                + " {\"id\": 2, \"name\": \"user2\", \"email\": \"user2@example.com\"}]";
        this.mockRestServiceServer.expect(requestTo("http://localhost:9090/users"))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> objectResponseEntity = this.client.getAllUsers();
        List<LinkedHashMap<String, String>> userList = (List<LinkedHashMap<String, String>>) objectResponseEntity
                .getBody();
        Assertions.assertNotNull(userList);
        Assertions.assertEquals(2, userList.size());
        Assertions.assertEquals(1, userList.get(0).get("id"));
        Assertions.assertEquals("user1", userList.get(0).get("name"));
        Assertions.assertEquals("user1@example.com", userList.get(0).get("email"));
        Assertions.assertEquals(2, userList.get(1).get("id"));
        Assertions.assertEquals("user2", userList.get(1).get("name"));
        Assertions.assertEquals("user2@example.com", userList.get(1).get("email"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void saveUser() {
        this.mockRestServiceServer.expect(requestTo("http://localhost:9090/users"))
                .andExpect(content().json(request))
                .andRespond(withSuccess(request, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> objectResponseEntity = this.client.saveUser(user);
        Assertions.assertNotNull(objectResponseEntity);
        Assertions.assertEquals(HttpStatus.OK, objectResponseEntity.getStatusCode());
        LinkedHashMap<String, String> userRes = (LinkedHashMap<String, String>) objectResponseEntity.getBody();
        Assertions.assertEquals(user.getName(), userRes.get("name"));
        Assertions.assertEquals(user.getEmail(), userRes.get("email"));
    }

    @Test
    void updateUser() throws JsonProcessingException {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("new@email.com");
        String request = objectMapper.writeValueAsString(user);
        this.mockRestServiceServer.expect(requestTo("http://localhost:9090/users/1"))
                .andRespond(withSuccess(request, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> objectResponseEntity = this.client.updateUser(1, userDTO);
        Assertions.assertNotNull(objectResponseEntity);
        Assertions.assertEquals(HttpStatus.OK, objectResponseEntity.getStatusCode());
    }

    @Test
    void removeUser() {
        this.mockRestServiceServer.expect(requestTo("http://localhost:9090/users/1"))
                .andRespond(withSuccess());
        this.client.removeUser(1);
    }
}