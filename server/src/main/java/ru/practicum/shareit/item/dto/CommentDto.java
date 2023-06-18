package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CommentDto {

    private final Long id;
    private final String text;
    private final String authorName;
    private final LocalDateTime created = LocalDateTime.now();
}
