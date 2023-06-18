package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {
    private Long id;
    @NotBlank(message = "Название вещи не может быть пустым")
    private  String name;
    @NotBlank(message = "Описание вещи не может быть пустым")
    private  String description;
    @NotNull(message = "Необходимо указать доступна ли вещь в аренду на данный момент")
    private  Boolean available;
    private Long requestId;
}