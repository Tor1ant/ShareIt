package ru.practicum.shareit.user.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UsersMapStructMapper;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersMapStructMapper mapper = Mappers.getMapper(UsersMapStructMapper.class);
    private final UserRepository repository;

    @Override
    public User getUser(long id) {
        User userForGet = repository.getUser(id);
        if (userForGet == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return userForGet;
    }

    @Override
    public List<User> getAllUsers() {
        return repository.findAllUsers();
    }

    @Override
    public User saveUser(User user) {
        return repository.saveUser(user);
    }

    @Override
    public User updateUser(long userId, UserDTO userDTO) {
        User user = mapper.userDtoToUser(userDTO);
        return repository.updateUser(userId, user);
    }

    @Override
    public void removeUser(long userId) {
        repository.deleteUser(userId);
    }
}