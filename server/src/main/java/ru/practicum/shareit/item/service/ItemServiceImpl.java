package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemResponseDto create(ItemDto itemDto, long userId) {
        Item item = ItemMapper.requestDtoToItem(itemDto);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id " + userId + " not found"));

        item.setOwner(user);

        if (itemDto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() ->
                            new NotFoundException("ItemRequest with id " + itemDto.getRequestId() + " not found"));
            item.setRequest(request);
        }

        Item createdItem = itemRepository.save(item);
        return ItemMapper.itemToResponseDtoWithOutComments(createdItem);
    }

    @Override
    @Transactional
    public ItemResponseDto update(ItemDto itemDtoDto, long itemId, long userId) {
        Item item = ItemMapper.requestDtoToItem(itemDtoDto);

        Item existingItem = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Item with id " + itemId + " not found"));

        if (!userRepository.existsById(userId)) {
           throw  new NotFoundException("User with id " + userId + " not found");
        }

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotOwnerException("Only item owner can modify it");
        }

        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);

        return ItemMapper.itemToResponseDtoWithOutComments(updatedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponseDto get(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));

        List<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::commentToResponseDto)
                .collect(Collectors.toList());

        boolean isOwner = item.getOwner().getId().equals(userId);

        if (isOwner) {
            BookingResponseDto lastBooking = bookingRepository.findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
                            itemId, BookingStatus.APPROVED, LocalDateTime.now())
                    .map(BookingMapper::bookingToResponseDto)
                    .orElse(null);
            BookingResponseDto nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                            itemId, BookingStatus.APPROVED, LocalDateTime.now())
                    .map(BookingMapper::bookingToResponseDto)
                    .orElse(null);
            return ItemMapper.itemToResponseDto(item, comments, lastBooking, nextBooking);
        } else {
            return ItemMapper.itemToResponseDto(item, comments, null, null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> getItemsByUser(long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);

        return items.stream()
                .map(ItemMapper::itemToResponseDtoWithOutComments)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(long itemId, long userId) {
        Item existingItem = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Item with id " + itemId + " not found"));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotOwnerException("Only item owner can delete it");
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> searchItems(String searchText) {
        if (searchText == null || searchText.isBlank()) {
            return List.of();
        }

        List<Item> items = itemRepository.search(searchText);
        return items.stream()
                .map(ItemMapper::itemToResponseDtoWithOutComments)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, long itemId, long userId) {
        Item existingItem = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Item with id " + itemId + " not found"));

        User existingAuthor = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id " + userId + " not found"));

        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());

        if (bookings.isEmpty()) {
            throw new ValidationException("Only users who have rented and completed this item can leave a review.");
        }

        Comment comment = CommentMapper.requestDtoToComment(commentDto);
        comment.setItem(existingItem);
        comment.setAuthor(existingAuthor);
        comment.setCreatedDate(LocalDateTime.now());

        Comment createdComment = commentRepository.save(comment);
        return CommentMapper.commentToResponseDto(createdComment);
    }
}