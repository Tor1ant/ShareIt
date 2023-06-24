package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.model.User;

@Mapper
public interface UsersMapStructMapper {

    User userDtoToUser(UserDTO userDTO);

}
