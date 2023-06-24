package ru.practicum.shareit.item;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDTO;

@JsonTest
class ItemDTOTest {
    @Autowired
    private JacksonTester<ItemDTO> json;

    @DisplayName("Тестирование сериализации в JSON")
    @Test
    void testSerializeToJson() throws IOException {
        ItemDTO dto = new ItemDTO(1L, "Название вещи", "Описание вещи", true, 2L);
        JsonContent<ItemDTO> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Название вещи");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание вещи");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
    }

    @DisplayName("Тестирование десериализации из JSON")
    @Test
    void testDeserializeFromJson() throws IOException {
        String jsonString = "{\"id\": 1, \"name\": \"Название вещи\", \"description\": \"Описание вещи\"," +
                " \"available\": true, \"requestId\": 2}";
        ItemDTO dto = json.parseObject(jsonString);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Название вещи");
        assertThat(dto.getDescription()).isEqualTo("Описание вещи");
        assertThat(dto.getAvailable()).isEqualTo(true);
        assertThat(dto.getRequestId()).isEqualTo(2L);
    }
}
