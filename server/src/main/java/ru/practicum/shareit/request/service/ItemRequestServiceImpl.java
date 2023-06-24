package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.RequestMapperMapstruct;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestData;
import ru.practicum.shareit.request.model.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestMapperMapstruct requestMapper = Mappers.getMapper(RequestMapperMapstruct.class);

    @Transactional
    @Override
    public ItemRequest postItemRequest(ItemRequestData itemRequestData) {
        User user = userRepository.findById(itemRequestData.getCreatorId()).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + itemRequestData.getCreatorId() + " не найден"));

        ItemRequest itemRequest = requestMapper.itemRequestDtoToItemRequest(itemRequestData.getInputDto());
        itemRequest.setRequester(user);
        requestRepository.save(itemRequest);
        log.info("Добавлено в базу данных itemRequest= " + itemRequest);
        return itemRequest;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDtoWithItems> getItemRequests(Long creatorId) {
        userValidation(creatorId);

        List<ItemRequestDtoWithItems> result = new ArrayList<>();

        Long userRequestCount = requestRepository.countAllByRequesterId(creatorId);
        if (userRequestCount == 0) {
            userRequestCount++;
        }

        Pageable sortedByCreated = PageRequest.of(0, userRequestCount.intValue(), Sort.by("created")
                .ascending());
        result = getItemRequestDtoWithItems(creatorId, result, sortedByCreated, true);
        log.info("Список полученных реквестов= " + result);
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDtoWithItems> getItemRequestsInPages(Long userId, Long from, Long size) {
        userValidation(userId);

        List<ItemRequestDtoWithItems> result = new ArrayList<>();

        PageRequest sortedByCreated = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0,
                size.intValue(), Sort.by("created").ascending());

        result = getItemRequestDtoWithItems(userId, result, sortedByCreated, false);
        log.info("Список полученных реквестов= " + result);
        return result;
    }

    private List<ItemRequestDtoWithItems> getItemRequestDtoWithItems(Long userId,
                                                                     List<ItemRequestDtoWithItems> result,
                                                                     Pageable sortedByCreated,
                                                                     boolean isCreator) {
        Map<Long, ItemRequest> userRequests;
        if (isCreator) {
            userRequests = requestRepository.findAllByRequesterIdOrderByCreatedAsc(userId, sortedByCreated).stream()
                    .collect(Collectors.toMap(ItemRequest::getId, itemRequest -> itemRequest));
        } else {
            userRequests = requestRepository.findAllByRequesterIdNot(userId, sortedByCreated).stream()
                    .collect(Collectors.toMap(ItemRequest::getId, itemRequest -> itemRequest));
        }
        if (!userRequests.isEmpty()) {
            result = getItemRequestDtoWithItems(userRequests);
        }
        log.info("Список полученных реквестов= " + result);
        return result;
    }

    private List<ItemRequestDtoWithItems> getItemRequestDtoWithItems(Map<Long, ItemRequest> itemRequests) {
        List<ItemRequestDtoWithItems> result = new ArrayList<>();

        ItemRequestDtoWithItems requestWithItems;

        List<Long> requestsId = new ArrayList<>(itemRequests.keySet());
        Map<Long, ItemForRequestDto> responseItems = itemRepository.findAllByRequests(requestsId)
                .stream()
                .collect(Collectors.toMap(ItemForRequestDto::getRequestId, item -> item));

        for (Long requestId : requestsId) {
            requestWithItems = requestMapper.itemRequestToItemRequestDtoWithItems(itemRequests.get(requestId));

            List<ItemForRequestDto> items = responseItems.values()
                    .stream()
                    .filter(item -> item.getRequestId().equals(requestId))
                    .collect(Collectors.toList());

            requestWithItems.setItems(items);
            result.add(requestWithItems);
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDtoWithItems getItemRequest(Long creatorId, Long itemRequestId) {
        validation(creatorId, itemRequestId);

        List<ItemRequestDtoWithItems> result = new ArrayList<>();
        Optional<ItemRequest> itemRequest = requestRepository.findById(itemRequestId);

        if (itemRequest.isPresent()) {
            result = getItemRequestDtoWithItems(Map.of(itemRequestId, itemRequest.get()));
        }
        log.info("Получен реквест= " + result.get(0));
        return result.get(0);
    }

    private void userValidation(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    private void validation(Long creatorId, Long itemRequestId) {
        if (!userRepository.existsById(creatorId)) {
            log.warn("Пользователь с id " + creatorId + " не найден");
            throw new NotFoundException("Пользователь с id " + creatorId + " не найден");
        }
        if (!requestRepository.existsById(itemRequestId)) {
            log.warn("Реквест с id " + itemRequestId + " не найден");
            throw new NotFoundException("Реквест с id " + itemRequestId + " не найден");
        }
    }
}
