package ru.practicum.shareit.request.model;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-item-requests.
 */
@RequiredArgsConstructor
@Data
public class ItemRequest {
    private int requestId;
    private final String description;
    private final User requester;
    private final LocalDateTime created;
}
