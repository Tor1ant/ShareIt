package ru.practicum.shareit.user.repository;

import java.util.List;
import ru.practicum.shareit.user.model.User;

public interface UserRepository {
    List<User> findAllUsers();

    User getUser(long userId);

    User saveUser(User user);

    User updateUser(long userId, User user);

    void deleteUser(long userId);
}