package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserDTO {
    @NotBlank(message = "email не может быть пустым")
    @Email(message = "не верный формат email")
    private String email;
    @NotBlank(message = "имя не может быть пустым")
    private String name;
}
