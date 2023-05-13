package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

@Mapper
public interface UsersMapStructMapper {

    UserDTO userToDto(User user);

    User userDtoToUser(UserDTO userDTO);

}
