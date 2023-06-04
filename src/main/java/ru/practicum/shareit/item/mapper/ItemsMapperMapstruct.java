package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemsMapperMapstruct {

    Item itemDtoToItem(ItemDTO itemDTO, long userId);

    ArrayList<ItemDTO> itemsToDTOItems(Collection<Item> itemDTOS);

    ItemDTO itemToItemDTO(Item item);

    ItemDtoWithBooking itemToItemDTOWithBookings(Item item, List<CommentDto> comments);

    List<ItemDtoWithBooking> itemsToItemsDtoWithBookings(List<Item> items);
}
