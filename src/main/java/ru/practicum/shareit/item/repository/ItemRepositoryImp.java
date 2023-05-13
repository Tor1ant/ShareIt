package ru.practicum.shareit.item.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemsMapperMapstruct;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;

@Repository
@Slf4j
public class ItemRepositoryImp implements ItemRepository {

    private long itemIdCounter = 0;
    Map<Long, List<Item>> itemMap = new HashMap<>();
    private final ItemsMapperMapstruct itemsMapper = Mappers.getMapper(ItemsMapperMapstruct.class);

    @Override
    public List<ItemDTO> getUserItems(long userId) {
        return itemsMapper.itemsToDTOItems(itemMap.getOrDefault(userId, Collections.emptyList()));
    }

    @Override
    public ItemDTO getItem(long itemId) {
        Item itemForGet;
        Optional<Item> optionalItems = itemMap.values().stream()
                .flatMap(List::stream)
                .filter(item -> item.getId() == itemId)
                .findFirst();
        if (optionalItems.isPresent()) {
            itemForGet = optionalItems.get();
        } else {
            throw new NotFoundException("Вещь не найдена");
        }
        return itemsMapper.itemToItemDTO(itemForGet);
    }

    @Override
    public ItemDTO save(Item item) {
        item.setId(++itemIdCounter);
        itemMap.compute(item.getUserId(), (userId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        return itemsMapper.itemToItemDTO(item);
    }

    @Override
    public ItemDTO update(Item item) {
        List<Item> items = itemMap.getOrDefault(item.getUserId(), Collections.emptyList());
        Optional<Item> optionalItem = items.stream().filter(i -> i.getId().equals(item.getId())).findFirst();
        if (!optionalItem.isPresent()) {
            throw new NotFoundException("Пользователь с id " + item.getId() + " не найден.");
        }
        Item oldItem = optionalItem.get();
        Item updatedItem = Item.builder()
                .id(oldItem.getId())
                .name(item.getName() != null ? item.getName() : oldItem.getName())
                .description(item.getDescription() != null ? item.getDescription() : oldItem.getDescription())
                .available(item.getAvailable() != null ? item.getAvailable() : oldItem.getAvailable())
                .build();
        itemMap.computeIfAbsent(item.getUserId(), k -> new ArrayList<>()).remove(oldItem);
        itemMap.computeIfAbsent(item.getUserId(), k -> new ArrayList<>()).add(updatedItem);
        return itemsMapper.itemToItemDTO(updatedItem);
    }


    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        if (itemMap.containsKey(userId)) {
            List<Item> userItemDTOS = itemMap.get(userId);
            userItemDTOS.removeIf(item -> item.getId().equals(itemId));
        }
    }

    @Override
    public List<ItemDTO> searchItem(String text) {
        List<Item> searchedItems = new ArrayList<>();
        itemMap.values().forEach(list -> list.forEach(item -> {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) && item.getAvailable() && !text.equals("")
                    || item.getDescription().toLowerCase().contains(text.toLowerCase()) && item.getAvailable()
                    && !text.equals("")) {
                searchedItems.add(item);
            }
        }));

        return itemsMapper.itemsToDTOItems(searchedItems);
    }
}
