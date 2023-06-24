package ru.practicum.shareit.item;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingIdAndBookerDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

@JsonTest
class ItemDtoWithBookingTest {
    @Autowired
    private JacksonTester<ItemDtoWithBooking> json;

    @DisplayName("Тестирование сериализации в JSON")
    @Test
    void testSerializeToJson() throws IOException {
        ItemDtoWithBooking dto = new ItemDtoWithBooking("Название вещи","Описание вещи",true);
        dto.setId(1L);

        BookingIdAndBookerDto lastBooking = new BookingIdAndBookerDto();
        lastBooking.setBookerId(1L);

        BookingIdAndBookerDto nextBooking = new BookingIdAndBookerDto();
        nextBooking.setBookerId(2L);

        List<CommentDto> comments = new ArrayList<>();
        CommentDto comment1 = new CommentDto(null,"комментарий 1","автор");
        CommentDto comment2 = new CommentDto(null,"комментарий 2","автор2");
        comments.add(comment1);
        comments.add(comment2);

        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        dto.setComments(comments);

        JsonContent<ItemDtoWithBooking> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Название вещи");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание вещи");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookingId").isEqualTo(null);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookingId").isEqualTo(null);
        assertThat(result).extractingJsonPathArrayValue("$.comments[*].text")
                .containsExactly("комментарий 1", "комментарий 2");
    }
}
