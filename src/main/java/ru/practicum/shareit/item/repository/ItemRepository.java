package ru.practicum.shareit.item.repository;

import java.util.List;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository {

    List<ItemDTO> getUserItems(long userId);

    ItemDTO getItem(long itemId);

    ItemDTO save(Item item);

    ItemDTO update(Item item);

    void deleteByUserIdAndItemId(long userId, long itemId);

    List<ItemDTO> searchItem(String text);
}