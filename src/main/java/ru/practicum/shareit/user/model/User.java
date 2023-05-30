package ru.practicum.shareit.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "users", schema = "public")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "email не может быть пустым")
    @Email(message = "не верный формат email")
    @Column(name = "email")
    private String email;
    @NotBlank(message = "имя не может быть пустым")
    @Column(name = "name")
    private String name;
}