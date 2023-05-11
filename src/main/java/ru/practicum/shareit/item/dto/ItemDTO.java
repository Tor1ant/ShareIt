package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemDTO {
    private Long id;
    @NotBlank(message = "Название вещи не может быть пустым")
    private final String name;
    @NotBlank(message = "Описание вещи не может быть пустым")
    private final String description;
    @NotNull(message = "Необходимо указать доступна ли вещь в аренду на данный момент")
    private final Boolean available;
}