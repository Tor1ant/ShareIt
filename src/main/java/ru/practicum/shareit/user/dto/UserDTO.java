package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserDTO {
    @NotBlank(message = "email не может быть пустым")
    @Email(message = "не верный формат email")
    private  String email;
    @NotBlank(message = "имя не может быть пустым")
    private  String name;
}
