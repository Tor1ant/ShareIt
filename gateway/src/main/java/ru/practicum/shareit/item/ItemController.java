package ru.practicum.shareit.item;


import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.shareit.item.dto.InputCommentDto;
import ru.practicum.shareit.item.dto.ItemDTO;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;
    public static final String CUSTOM_USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId) {
        return itemClient.getUserItems(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
            @PathVariable long itemId) {
        return itemClient.getItem(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(CUSTOM_USER_ID_HEADER) Long userId,
            @RequestBody @Valid ItemDTO itemDTO) {
        return itemClient.addItem(userId, itemDTO);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
            @RequestBody ItemDTO itemDTO,
            @PathVariable long itemId) {
        if (itemDTO.getId() == null) {
            itemDTO.setId(itemId);
        }
        return itemClient.update(userId, itemDTO, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId, @PathVariable long itemId) {
        itemClient.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
            @RequestParam String text) {
        return itemClient.searchItem(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(CUSTOM_USER_ID_HEADER) long userId,
            @RequestBody @Valid InputCommentDto text, @PathVariable long itemId) {
        return itemClient.createComment(userId, itemId, text);
    }
}