package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;

import javax.validation.constraints.NotNull;

@Data
public class ItemRequestData {
    @NotNull(message = "входящий реквест не может быть null")
    private final ItemRequestInputDto inputDto;
    @NotNull(message = "у реквеста должен быть автор")
    private final Long creatorId;
}
