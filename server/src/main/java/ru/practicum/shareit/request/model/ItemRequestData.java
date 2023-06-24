package ru.practicum.shareit.request.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;

@Data
public class ItemRequestData {
    @NotNull(message = "входящий реквест не может быть null")
    private final ItemRequestInputDto inputDto;
    @NotNull(message = "у реквеста должен быть автор")
    private final Long creatorId;
}
