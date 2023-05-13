package ru.practicum.shareit.item.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemsMapperMapstruct;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemsMapperMapstruct itemsMapper = Mappers.getMapper(ItemsMapperMapstruct.class);


    @Override
    public ItemDTO getItem(long itemId) {
        return itemRepository.getItem(itemId);
    }

    @Override
    public List<ItemDTO> searchItem(String text) {
        return itemRepository.searchItem(text);

    }

    @Override
    public List<ItemDTO> getUserItems(long userId) {
        return itemRepository.getUserItems(userId);
    }

    @Override
    public ItemDTO addItem(Long userId, ItemDTO itemDTO) {
        if (userId == null || userRepository.getUser(userId) == null) {
            throw new NotFoundException("Пользователь для добавления вещи не найден");
        }
        Item item = itemsMapper.itemDtoToItem(itemDTO, userId);
        return itemRepository.save(item);
    }

    @Override
    public ItemDTO update(Long userID, ItemDTO itemDTO) {
        try {
            userRepository.getUser(userID);
        } catch (NullPointerException e) {
            throw new NullPointerException();
        }
        Item item = itemsMapper.itemDtoToItem(itemDTO, userID);

        return itemRepository.update(item);
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }
}
