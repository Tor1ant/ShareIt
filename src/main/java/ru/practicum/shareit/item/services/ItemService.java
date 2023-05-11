package ru.practicum.shareit.item.services;

import java.util.List;
import ru.practicum.shareit.item.dto.ItemDTO;

public interface ItemService {

    List<ItemDTO> getUserItems(long userId);

    ItemDTO addItem(Long userId, ItemDTO itemDTO);

    ItemDTO update(Long userID, ItemDTO itemDTO);

    void deleteItem(long userId, long itemId);

    ItemDTO getItem(long itemId);

    List<ItemDTO> searchItem(String text);
}
