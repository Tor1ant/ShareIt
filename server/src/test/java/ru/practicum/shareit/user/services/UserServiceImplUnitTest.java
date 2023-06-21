package ru.practicum.shareit.user.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    @DisplayName("Проверка получения пользователя")
    @Test
    void getUserTest() {
        User user = new User();
        user.setName("testUser");
        user.setEmail("test@email.ru");

        Assertions.assertNull(user.getId());
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> {
                    user.setId(1L);
                    return Optional.of(user);
                });
        Assertions.assertEquals(1, userService.getUser(1).getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @DisplayName("Проверка получения несуществующего пользователя")
    @Test
    void getUserWithException() {
        User user = new User();
        user.setName("testUser");
        user.setEmail("test@email.ru");

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> userService.getUser(1));
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @DisplayName("Проверка получения Всех пользователей пользователя")
    @Test
    void getAllUsersTest() {
        User user = new User();
        user.setName("testUser");
        user.setEmail("test@email.ru");
        User user2 = new User();
        user2.setName("testUser2");
        user2.setEmail("test@email.ru2");

        Mockito.when(userRepository.findAll()).thenAnswer(invocationOnMock -> {
            user.setId(1L);
            user2.setId(2L);
            return List.of(user, user2);
        });

        Assertions.assertEquals(List.of(user, user2), userService.getAllUsers());
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }

    @DisplayName("Проверка сохранения пользователя")
    @Test
    void saveUser() {
        User user = new User();
        user.setName("testUser");
        user.setEmail("test@email.ru");

        Mockito.when(userRepository.save(user)).thenAnswer(invocationOnMock -> {
            user.setId(1L);
            return user;
        });
        Assertions.assertEquals(1, userService.saveUser(user).getId());
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @DisplayName("Проверка обновления несуществующего пользователя")
    @Test
    void updateUserWithWrongUser() {
        long userId = 1L;
        UserDTO user = new UserDTO("test@email.ru", "testUser");

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> userService.updateUser(userId, user));
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @DisplayName("Проверка обновления существующего email пользователя")
    @Test
    void updateUserWithWrongEmail() {
        long userId = 1L;
        UserDTO user = new UserDTO("test@email.ru", "testUser");
        User userFromDb = new User();
        userFromDb.setEmail(user.getEmail());
        userFromDb.setName(user.getName());
        userFromDb.setId(userId);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(userFromDb));
        Mockito.when(userRepository.existsByEmailAndIdNot(Mockito.anyString(), Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> true);


        Assertions.assertThrows(ConflictException.class, () -> userService.updateUser(userId, user));
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @DisplayName("Проверка обновления имени и email пользователя")
    @Test
    void updateUserWithNameAndEmail() {
        long userId = 1L;
        UserDTO user = new UserDTO("test@email.ru{Updated}", "testUser{Updated}");
        User userFromDb = new User();
        userFromDb.setEmail("test@email.ru");
        userFromDb.setName("testUser");
        userFromDb.setId(userId);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(userFromDb));
        Mockito.when(userRepository.existsByEmailAndIdNot(Mockito.anyString(), Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> false);

        User userResult = userService.updateUser(userId, user);

        Assertions.assertEquals(user.getEmail(), userResult.getEmail());
        Assertions.assertEquals(user.getName(), userResult.getName());
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @DisplayName("Проверка обновления имени пользователя")
    @Test
    void updateUserWithName() {
        long userId = 1L;
        UserDTO user = new UserDTO(null, "testUser{Updated}");
        User userFromDb = new User();
        userFromDb.setEmail("test@email.ru");
        userFromDb.setName("testUser");
        userFromDb.setId(userId);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(userFromDb));

        User userResult = userService.updateUser(userId, user);

        Assertions.assertEquals(user.getName(), userResult.getName());
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @DisplayName("Проверка обновления email пользователя")
    @Test
    void updateUserWithEmail() {
        long userId = 1L;
        UserDTO user = new UserDTO("updatedEmail", null);
        User userFromDb = new User();
        userFromDb.setEmail("test@email.ru");
        userFromDb.setName("testUser");
        userFromDb.setId(userId);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(userFromDb));

        User userResult = userService.updateUser(userId, user);

        Assertions.assertEquals(user.getEmail(), userResult.getEmail());
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @DisplayName("Проверка удаления существующего  пользователя")
    @Test
    void removeUser() {
        userService.removeUser(1);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
    }
}