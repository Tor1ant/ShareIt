package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestData;
import ru.practicum.shareit.request.model.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.item.ItemController.CUSTOM_USER_ID_HEADER;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestServiceImpl itemRequestService;

    @PostMapping()
    ItemRequest postItemRequest(@RequestHeader(CUSTOM_USER_ID_HEADER) Long creatorId,
                                @RequestBody @Valid ItemRequestInputDto inputDto) {

        ItemRequestData itemRequestData = new ItemRequestData(inputDto, creatorId);
        return itemRequestService.postItemRequest(itemRequestData);
    }

    @GetMapping()
    List<ItemRequestDtoWithItems> getItemsRequest(@RequestHeader(CUSTOM_USER_ID_HEADER) Long creatorId) {
        return itemRequestService.getItemRequests(creatorId);
    }

    @GetMapping("/all")
    List<ItemRequestDtoWithItems> getItemsRequestInPages(@RequestHeader(CUSTOM_USER_ID_HEADER) Long userId,
                                                         @RequestParam(defaultValue = "0") Long from,
                                                         @RequestParam(defaultValue = "10") Long size
    ) {
        if (from < 0) {
            throw new BadRequestException("Индекс первого элемента не может быть меньше нуля");
        }
        if (size < 0 || size == 0) {
            throw new BadRequestException("Количество элементов для отображения не может быть меньше или равно нулю");
        }
        return itemRequestService.getItemRequestsInPages(userId, from, size);
    }

    @GetMapping("{requestId}")
    ItemRequestDtoWithItems getItemRequest(@RequestHeader(CUSTOM_USER_ID_HEADER) Long userId,
                                           @PathVariable Long requestId) {
        return itemRequestService.getItemRequest(userId, requestId);

    }
}
