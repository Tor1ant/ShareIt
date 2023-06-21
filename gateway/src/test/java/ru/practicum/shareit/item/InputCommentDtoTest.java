package ru.practicum.shareit.item;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.InputCommentDto;

@JsonTest
class InputCommentDtoTest {
    @Autowired
    private JacksonTester<InputCommentDto> json;

    @DisplayName("Тестирование сериализации в JSON")
    @Test
    void testSerializeToJson() throws IOException {
        InputCommentDto dto = new InputCommentDto("Пример комментария");
        JsonContent<InputCommentDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Пример комментария");
    }

    @DisplayName("Тестирование десериализации из JSON")
    @Test
    void testDeserializeFromJson() throws IOException {
        String jsonString = "{\"text\":\"Пример комментария\"}";
        InputCommentDto dto = json.parseObject(jsonString);

        assertThat(dto.getText()).isEqualTo("Пример комментария");
    }
}

