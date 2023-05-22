package ru.practicum.shareit.item;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputCommentDto;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.services.ItemService;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    public static final String CUSTOM_USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemDtoWithBooking> getUserItems(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId) {
        return itemService.getUserItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking getItem(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId, @PathVariable long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @PostMapping
    public ItemDTO add(@RequestHeader(CUSTOM_USER_ID_HEADER) Long userId, @RequestBody @Valid ItemDTO itemDTO) {
        return itemService.addItem(userId, itemDTO);
    }

    @PatchMapping("/{itemId}")
    public ItemDTO update(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId, @RequestBody ItemDTO itemDTO,
            @PathVariable long itemId) {
        if (itemDTO.getId() == null) {
            itemDTO.setId(itemId);
        }
        return itemService.update(userId, itemDTO);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId, @PathVariable long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDTO> searchItem(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId, @RequestParam String text) {
        return itemService.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
            @RequestBody @Valid InputCommentDto text, @PathVariable long itemId) {
        return itemService.createComment(userId, itemId, text);
    }
}