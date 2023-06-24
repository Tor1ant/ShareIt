package ru.practicum.shareit.item.model;


import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {

    private Long id;
    private String text;
    private Item item;
    private User author;
    private LocalDateTime created;
}
