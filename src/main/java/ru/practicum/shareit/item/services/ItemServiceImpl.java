package ru.practicum.shareit.item.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapperMapstruct;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputCommentDto;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.CommentMapperMapstruct;
import ru.practicum.shareit.item.mapper.ItemsMapperMapstruct;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemsMapperMapstruct itemsMapper = Mappers.getMapper(ItemsMapperMapstruct.class);
    private final BookingMapperMapstruct bookingMapper = Mappers.getMapper(BookingMapperMapstruct.class);
    private final CommentMapperMapstruct commentMapper = Mappers.getMapper(CommentMapperMapstruct.class);

    @Transactional(readOnly = true)
    @Override
    public List<ItemDtoWithBooking> getUserItems(long userId) {
        List<Item> items = itemRepository.findItemsByOwnerIdOrderByIdAsc(userId);
        List<ItemDtoWithBooking> itemDtoWithBookings = itemsMapper.itemsToItemsDtoWithBookings(items);
        List<Long> itemsId = items.stream().map(Item::getId).collect(Collectors.toList());
        Map<Long, List<Booking>> bookingsMap = bookingRepository.findBookingsByItemId(itemsId).stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        log.info("itemDtoWithBookings до установки last & next bookings = " + itemDtoWithBookings);
        setLastAndNextBookings(itemDtoWithBookings, bookingsMap);
        log.info("itemDtoWithBookings после установки last & next bookings = " + itemDtoWithBookings);
        return itemDtoWithBookings;
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDtoWithBooking getItem(long userId, long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (!optionalItem.isPresent()) {
            throw new NotFoundException("Item не с id " + itemId + " не найден");
        }
        List<CommentDto> comments = commentMapper.commentToCommentDto(commentRepository.findAllByItemId(itemId));

        ItemDtoWithBooking itemDtoWithBooking = itemsMapper.itemToItemDTOWithBookings(optionalItem.get(), comments);
        if (itemRepository.getItemByIdAndOwnerId(itemId, userId).isPresent()) {
            Optional<List<Booking>> ownerBookings = bookingRepository.findBookingsByItemIdOrderByEndDesc(itemId);

            if (ownerBookings.isPresent()) {
                Map<Long, List<Booking>> bookingsMap = ownerBookings.get().stream()
                        .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
                log.info("itemDtoWithBooking до установки last & next bookings = " + itemDtoWithBooking);
                setLastAndNextBookings(Collections.singletonList(itemDtoWithBooking), bookingsMap);
                log.info("itemDtoWithBooking после установки last & next bookings = " + itemDtoWithBooking);
            }
        }
        return itemDtoWithBooking;
    }

    private void setLastAndNextBookings(List<ItemDtoWithBooking> itemDtoWithBookings,
            Map<Long, List<Booking>> bookingsMap) {
        LocalDateTime currentTime = LocalDateTime.now();

        itemDtoWithBookings.forEach(itemDtoWithBooking -> {
            Long itemId = itemDtoWithBooking.getId();
            List<Booking> itemBookings = bookingsMap.getOrDefault(itemId, Collections.emptyList());

            Optional<Booking> lastBooking = itemBookings.stream()
                    .filter(booking -> booking.getStart().isBefore(currentTime) && booking.getEnd().isAfter(currentTime)
                            || booking.getEnd().isBefore(currentTime))
                    .findFirst();
            if (!lastBooking.isPresent()) {
                return;
            }

            Optional<Booking> nextBooking = itemBookings.stream()
                    .filter(booking -> booking.getStart().isAfter(lastBooking.map(Booking::getEnd).orElse(null)))
                    .min(Comparator.comparing(Booking::getStart));

            nextBooking.ifPresent(
                    b -> itemDtoWithBooking.setNextBooking(bookingMapper.bookingToBookingDoIdAndOwnerId(b)));
            lastBooking.ifPresent(
                    b -> itemDtoWithBooking.setLastBooking(bookingMapper.bookingToBookingDoIdAndOwnerId(b)));
            log.info("nextBooking= " + nextBooking);
            log.info("lastBooking= " + lastBooking);
        });
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDTO> searchItem(String text) {
        if (text.trim().isEmpty()) {
            return new ArrayList<>();
        }
        Optional<List<Item>> items = itemRepository.search(text);
        if (!items.isPresent()) {
            throw new NotFoundException(" По вашему запросу ничего не найдено");
        }
        List<ItemDTO> searchedItems = itemsMapper.itemsToDTOItems((items.get()));
        log.info("searchedItems= " + searchedItems);
        return searchedItems;

    }

    @Transactional()
    @Override
    public ItemDTO addItem(Long userId, ItemDTO itemDTO) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new NotFoundException("Пользователь для добавления вещи не найден");
        }
        Item item = itemsMapper.itemDtoToItem(itemDTO, userId);
        item.setOwner(user.get());
        item = itemRepository.save(item);
        itemDTO.setId(item.getId());
        log.info("ItemDto добавленный в б/д= " + itemDTO);
        return itemDTO;
    }

    @Transactional()
    @Override
    public ItemDTO update(Long userID, ItemDTO itemDTO) {
        Optional<User> user = userRepository.findById(userID);
        if (!user.isPresent()) {
            throw new NotFoundException("Пользователь  с id " + user + " не найден");
        }
        Item oldItem = itemRepository.findItemByIdAndOwnerId(itemDTO.getId(), userID);
        log.info("Item до обновления в б/д= " + oldItem);
        Item updatedItem = Item.builder()
                .id(oldItem.getId())
                .name(itemDTO.getName() != null ? itemDTO.getName() : oldItem.getName())
                .description(itemDTO.getDescription() != null ? itemDTO.getDescription() : oldItem.getDescription())
                .available(itemDTO.getAvailable() != null ? itemDTO.getAvailable() : oldItem.getAvailable())
                .owner(oldItem.getOwner())
                .build();
        itemRepository.save(updatedItem);
        log.info("Item после обновления в б/д= " + updatedItem);
        return itemsMapper.itemToItemDTO(updatedItem);
    }

    @Transactional()
    @Override
    public void deleteItem(long userId, long itemId) {
        itemRepository.deleteByIdAndOwnerId(itemId, userId);
        log.info("item с id " + itemId + " удалён");
    }

    @Transactional()
    @Override
    public CommentDto createComment(long userId, long itemId, InputCommentDto input) {
        List<Booking> booking = bookingRepository.findBookingByBookerIdAndItemIdAndStatusApproved(userId, itemId);
        AtomicReference<CommentDto> commentToSend = new AtomicReference<>();
        if (booking.isEmpty()) {
            throw new BadRequestException("Вы не брали эту вещь в аренду");
        }
        Optional<Item> item = itemRepository.findById(itemId);
        item.ifPresent(i -> {
            Optional<User> author = userRepository.findById(userId);
            Comment comment = Comment.builder()
                    .author(author.orElseThrow())
                    .text(input.getText())
                    .item(i)
                    .build();
            commentToSend.set(commentMapper.commentToCommentDto(commentRepository.save(comment)));
        });
        CommentDto commentDto = commentToSend.get();
        log.info("commentDto добавленный в б/д= " + commentDto);
        return commentDto;
    }
}
