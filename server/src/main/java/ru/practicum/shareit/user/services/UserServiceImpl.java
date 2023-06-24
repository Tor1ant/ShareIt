package ru.practicum.shareit.user.services;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.UsersMapStructMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UsersMapStructMapper mapper = Mappers.getMapper(UsersMapStructMapper.class);
    private final UserRepository repository;

    @Transactional(readOnly = true)
    @Override
    public User getUser(long id) {
        Optional<User> userForGet = repository.findById(id);
        if (!userForGet.isPresent()) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        User userToSend = userForGet.get();
        log.info("userToSend= " + userToSend);
        return userToSend;
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getAllUsers() {
        List<User> userToSent = repository.findAll();
        log.info("Список полученных пользователей= " + userToSent);
        return userToSent;
    }

    @Transactional()
    @Override
    public User saveUser(User user) {
        User userFromBd;
        try {
            userFromBd = repository.save(user);
        } catch (Exception e) {
            throw new ConflictException("пользователь с " + user.getEmail() + " уже существует.");
        }
        log.info("Пользователь сохранён в базу данных= " + userFromBd);
        return userFromBd;
    }

    @Transactional()
    @Override
    public User updateUser(long userId, UserDTO userDTO) {
        User userForUpdate = mapper.userDtoToUser(userDTO);
        Optional<User> userFromBd = repository.findById(userId);
        if (!userFromBd.isPresent()) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        if (userForUpdate.getEmail() != null) {
            if (repository.existsByEmailAndIdNot(userForUpdate.getEmail(), userId)) {
                throw new ConflictException("пользователь с таким email уже существует");
            }
        }
        if (userForUpdate.getEmail() != null && userForUpdate.getName() != null) {
            userFromBd.get().setId(userId);
            userFromBd.get().setName(userForUpdate.getName());
            userFromBd.get().setEmail(userForUpdate.getEmail());
        } else if (userForUpdate.getName() != null) {
            userFromBd.get().setName(userForUpdate.getName());
        } else if (userForUpdate.getEmail() != null) {
            userFromBd.get().setEmail(userForUpdate.getEmail());
        }
        User userToSave = userFromBd.get();
        repository.save(userToSave);
        log.info("Пользователь сохранён в базу данных= " + userToSave);
        return userToSave;
    }

    @Transactional()
    @Override
    public void removeUser(long userId) {
        repository.deleteById(userId);
        log.info("Пользователь с id " + userId + " удалён из базы данных");
    }
}