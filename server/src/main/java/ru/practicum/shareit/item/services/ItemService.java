package ru.practicum.shareit.item.services;

import java.util.List;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputCommentDto;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

public interface ItemService {

    List<ItemDtoWithBooking> getUserItems(long userId);

    ItemDTO addItem(Long userId, ItemDTO itemDTO);

    ItemDTO update(Long userID, ItemDTO itemDTO);

    void deleteItem(long userId, long itemId);

    ItemDtoWithBooking getItem(long userId, long itemId);

    List<ItemDTO> searchItem(String text);

    CommentDto createComment(long userId, long itemId, InputCommentDto input);
}
