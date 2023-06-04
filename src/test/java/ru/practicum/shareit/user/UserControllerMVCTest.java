package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.services.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerMVCTest {
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;
    private User user;
    private UserDTO userDTO;
    private MockMvc mvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(userController).build();
        user = new User();
        user.setEmail("test@mail.ru");
        user.setName("testName");
        userDTO = new UserDTO("newName","newMail@.ru");
    }

    @Test
    void getUser() throws Exception {

        Mockito.when(userService.getUser(Mockito.anyLong())).thenReturn(user);
        mvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(user.getEmail()), String.class))
                .andExpect(jsonPath("$.name", is(user.getName()), String.class));
    }

    @Test
    void getAllUsers() throws Exception {
        Mockito.when(userService.getAllUsers()).thenReturn(List.of(user));
        mvc.perform(get("/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.[0].email", is(user.getEmail()), String.class))
                .andExpect(jsonPath("$.[0].name", is(user.getName()), String.class));
    }

    @DisplayName("Проверка сохранения нового пользователя без ошибок")
    @Test
    void saveNewUserTest() throws Exception {
        Mockito.when(userService.saveUser(Mockito.any())).thenReturn(user);
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(user.getEmail()), String.class))
                .andExpect(jsonPath("$.name", is(user.getName()), String.class));
    }

    @DisplayName("Проверка сохранения нового пользователя с занятым email")
    @Test
    void saveNewUserWithExistingEmail() throws Exception {
        Mockito.when(userService.saveUser(Mockito.any())).thenThrow(DataIntegrityViolationException.class);
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateUser() throws Exception {
        Mockito.when(userService.updateUser(Mockito.anyLong(), Mockito.any())).thenReturn(user);
        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(user.getEmail()), String.class))
                .andExpect(jsonPath("$.name", is(user.getName()), String.class));
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/users/1")
                        .accept(objectMapper.writeValueAsString("1"))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(userService, Mockito.times(1)).removeUser(Mockito.anyLong());
    }
}