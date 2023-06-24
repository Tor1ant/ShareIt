package ru.practicum.shareit.user.services;

import java.util.List;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.model.User;

@Service
public interface UserService {
    User getUser(long id);

    List<User> getAllUsers();

    User saveUser(User user);

    User updateUser(long userId, UserDTO user);

    void removeUser(long userId);
}