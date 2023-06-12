package ru.practicum.shareit.request.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

@JsonTest
class ItemRequestInputDtoTest {

    @Autowired
    private JacksonTester<ItemRequestInputDto> json;

    @Test
    void itemRequestInputDtoTest() throws IOException {
        ItemRequestInputDto inputDto = new ItemRequestInputDto("тестовое описание", null);
        JsonContent<ItemRequestInputDto> result = json.write(inputDto);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("тестовое описание");
    }
}