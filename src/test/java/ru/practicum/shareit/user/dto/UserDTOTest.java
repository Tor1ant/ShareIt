package ru.practicum.shareit.user.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

@JsonTest
class UserDTOTest {
    @Autowired
    private JacksonTester<UserDTO> json;

    @DisplayName("Тестирование  пользователя")
    @Test
    void userJsonTest() throws IOException {
        UserDTO userDTO = new UserDTO("testEmail", "testName");
        JsonContent<UserDTO> result = json.write(userDTO);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("testEmail");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("testName");
    }
}