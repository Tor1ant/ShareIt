package ru.practicum.shareit.item;

import java.util.ArrayList;
import java.util.Collection;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;

public class ItemsMapper {

    public static Item itemDtoToItem(ItemDTO itemDTO, long userId) {
        return new Item(itemDTO.getId(),userId, itemDTO.getName(), itemDTO.getDescription(),
                itemDTO.getAvailable());

    }

    public static ArrayList<ItemDTO> itemsToDTOItems(Collection<Item> itemDTOS) {
        ArrayList<ItemDTO> arrayList = new ArrayList<>();
        itemDTOS.forEach(itemDTO -> arrayList.add(ItemsMapper.itemToItemDTO(itemDTO)));
        return arrayList;
    }

    public static ItemDTO itemToItemDTO(Item item) {
        ItemDTO itemDTO = new ItemDTO(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
        itemDTO.setId(item.getId());
        return itemDTO;
    }
}
