package ru.practicum.shareit.item;

import java.util.ArrayList;
import java.util.Collection;
import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemsMapperMapstruct {

    Item itemDtoToItem(ItemDTO itemDTO, long userId);

    ArrayList<ItemDTO> itemsToDTOItems(Collection<Item> itemDTOS);

    ItemDTO itemToItemDTO(Item item);

}
