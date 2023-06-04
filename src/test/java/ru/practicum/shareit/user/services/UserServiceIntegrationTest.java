package ru.practicum.shareit.user.services;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Transactional
@SpringBootTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceIntegrationTest {

    private final UserRepository userRepository;
    private final UserService userService;

    private User user;
    private User user2;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("testUser");
        user.setEmail("test@email.ru");
        user2 = new User();
        user2.setName("testUser2");
        user2.setEmail("test@email.ru2");
    }

    @DisplayName("Проверка создания пользователя")
    @Test
    void saveUser() {
        Assertions.assertNull(user.getId());
        User userFromBd = userRepository.save(user);
        Assertions.assertEquals(user, userFromBd);
    }

    @DisplayName("Проверка получения пользователя")
    @Test
    void getUserTest() {
        Assertions.assertNull(user.getId());
        userRepository.save(user);
        Assertions.assertEquals(1, userService.getUser(1).getId());
        Assertions.assertEquals(user, userService.getUser(1));
    }

    @DisplayName("Проверка получения Всех пользователей пользователя")
    @Test
    void getAllUsersTest() {
        userRepository.save(user);
        userRepository.save(user2);
        Assertions.assertEquals(List.of(user, user2), userService.getAllUsers());
    }

    @DisplayName("Проверка обновления имени и email пользователя")
    @Test
    void updateUserWithNameAndEmail() {
        long userId = 1L;
        UserDTO userDTO = new UserDTO("test@email.ru{Updated}", "testUser{Updated}");
        userRepository.save(user);
        User userResult = userService.updateUser(userId, userDTO);
        Assertions.assertEquals(userDTO.getEmail(), userResult.getEmail());
        Assertions.assertEquals(userDTO.getName(), userResult.getName());
    }

    @DisplayName("Проверка удаления существующего  пользователя")
    @Test
    void removeUser() {
        userRepository.save(user);
        userService.removeUser(1);
        Optional<User> optionalUser = userRepository.findById(1L);
        Assertions.assertFalse(optionalUser.isPresent());
    }
}