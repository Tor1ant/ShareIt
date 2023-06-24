package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDtoWithItems;

@Mapper(componentModel = "spring")
public interface RequestMapperMapstruct {

    ItemRequest itemRequestDtoToItemRequest(ItemRequestInputDto itemRequestInputDto);

    ItemRequestDtoWithItems itemRequestToItemRequestDtoWithItems(ItemRequest itemRequest);
}
