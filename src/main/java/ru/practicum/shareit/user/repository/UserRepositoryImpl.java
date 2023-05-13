package ru.practicum.shareit.user.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private long id = 1;
    private final HashMap<Long, User> users = new HashMap<>();
    private final Map<Long, String> usersEmails = new HashMap<>();

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(long userId) {
        return users.getOrDefault(userId, null);
    }

    @Override
    public User saveUser(User user) {
        long userId = id;
        checkEmail(userId, user.getEmail());
        id++;
        user.setId(userId);
        users.put(userId, user);
        usersEmails.put(userId, user.getEmail());
        return user;
    }

    @Override
    public User updateUser(long userId, User user) {
        if (users.containsKey(userId)) {
            checkEmail(userId, user.getEmail());
            if (user.getEmail() != null && user.getName() != null) {
                user.setId(userId);
                users.put(userId, user);
                usersEmails.put(userId, user.getEmail());
            } else if (user.getEmail() == null && user.getName() != null) {
                users.get(userId).setName(user.getName());
            } else if (user.getEmail() != null) {
                users.get(userId).setEmail(user.getEmail());
                usersEmails.put(userId, user.getEmail());
            }
        } else {
            throw new NotFoundException("пользователь для обновления не найден.");
        }
        return users.get(userId);
    }

    @Override
    public void deleteUser(long userId) {
        usersEmails.remove(userId);
        users.remove(userId);
    }

    private void checkEmail(long userId, String email) {
        if (usersEmails.containsValue(email) && !usersEmails.get(userId).equals(email)) {
            throw new ConflictException("пользователь с таким email уже существует");
        }
    }
}
