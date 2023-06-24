package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestData;
import ru.practicum.shareit.request.model.ItemRequestDtoWithItems;

import java.util.List;

public interface ItemRequestService {
    ItemRequest postItemRequest(ItemRequestData inputDto);

    List<ItemRequestDtoWithItems> getItemRequests(Long creatorId);

    List<ItemRequestDtoWithItems> getItemRequestsInPages(Long creatorId, Long from, Long size);

    ItemRequestDtoWithItems getItemRequest(Long creatorId, Long itemRequest);
}
