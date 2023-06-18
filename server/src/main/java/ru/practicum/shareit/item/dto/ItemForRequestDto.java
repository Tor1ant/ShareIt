package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemForRequestDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private final Long requestId;

    public ItemForRequestDto(Long id, String name, String description, Boolean available, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}