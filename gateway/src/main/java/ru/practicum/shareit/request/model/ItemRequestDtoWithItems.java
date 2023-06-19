package ru.practicum.shareit.request.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForRequestDto;

@Data
public class ItemRequestDtoWithItems {

    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemForRequestDto> items;
}
