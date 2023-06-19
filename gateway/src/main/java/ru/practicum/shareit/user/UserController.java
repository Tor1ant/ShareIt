package ru.practicum.shareit.user;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserClient userClient;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> saveNewUser(@RequestBody @Valid User user) {
        try {
            ResponseEntity<Object> userToSend = userClient.saveUser(user);
            HttpStatus statusCode = userToSend.getStatusCode();
            if (statusCode.is5xxServerError()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Пользователь с email=" + user.getEmail() + " уже существует.");
            }
            return ResponseEntity.ok(userToSend.getBody());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("email занят");
        }
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable int userId, @RequestBody UserDTO userDTO) {
        return userClient.updateUser(userId, userDTO);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userClient.removeUser(userId);
    }
}