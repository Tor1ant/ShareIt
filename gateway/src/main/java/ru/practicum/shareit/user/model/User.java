package ru.practicum.shareit.user.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class User {

    private Long id;
    @NotBlank(message = "email не может быть пустым")
    @Email(message = "не верный формат email")
    private String email;
    @NotBlank(message = "имя не может быть пустым")
    private String name;
}