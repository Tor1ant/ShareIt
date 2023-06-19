package ru.practicum.shareit.request;


import static ru.practicum.shareit.item.ItemController.CUSTOM_USER_ID_HEADER;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping()
    ResponseEntity<Object> postItemRequest(@RequestHeader(CUSTOM_USER_ID_HEADER) Long creatorId,
            @RequestBody @Valid ItemRequestInputDto inputDto) {

        return itemRequestClient.postItemRequest(inputDto,creatorId);
    }

    @GetMapping()
    ResponseEntity<Object> getItemsRequest(@RequestHeader(CUSTOM_USER_ID_HEADER) Long creatorId) {
        return itemRequestClient.getItemRequests(creatorId);
    }

    @GetMapping("/all")
    ResponseEntity<Object> getItemsRequestInPages(@RequestHeader(CUSTOM_USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "0") Long from,
            @RequestParam(defaultValue = "10") Long size
    ) {
        if (from < 0) {
            throw new BadRequestException("Индекс первого элемента не может быть меньше нуля");
        }
        if (size < 0 || size == 0) {
            throw new BadRequestException("Количество элементов для отображения не может быть меньше или равно нулю");
        }
        return itemRequestClient.getItemRequestsInPages(userId, from, size);
    }

    @GetMapping("{requestId}")
    ResponseEntity<Object> getItemRequest(@RequestHeader(CUSTOM_USER_ID_HEADER) Long userId,
            @PathVariable Long requestId) {
        return itemRequestClient.getItemRequest(userId, requestId);
    }
}
