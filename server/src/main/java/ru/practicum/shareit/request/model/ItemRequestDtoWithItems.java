package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDtoWithItems {

    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemForRequestDto> items;
}
