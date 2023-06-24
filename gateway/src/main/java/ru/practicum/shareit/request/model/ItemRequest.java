package ru.practicum.shareit.request.model;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

@Data
@NoArgsConstructor
public class ItemRequest {

    private Long id;
    @NotNull(message = "описание не может быть null")
    private String description;
    @NotNull(message = "у реквеста должен быть автор")
    private User requester;
    private LocalDateTime created;
}
