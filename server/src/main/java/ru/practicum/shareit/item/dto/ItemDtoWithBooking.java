package ru.practicum.shareit.item.dto;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingIdAndBookerDto;


@Data
public class ItemDtoWithBooking {

    private Long id;
    @NotBlank(message = "Название вещи не может быть пустым")
    private final String name;
    @NotBlank(message = "Описание вещи не может быть пустым")
    private final String description;
    @NotNull(message = "Необходимо указать доступна ли вещь в аренду на данный момент")
    private final Boolean available;
    private BookingIdAndBookerDto lastBooking;
    private BookingIdAndBookerDto nextBooking;
    private List<CommentDto> comments;
}
