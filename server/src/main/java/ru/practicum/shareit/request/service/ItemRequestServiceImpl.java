package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestResponseDto create(ItemRequestDto itemDto, long userId) {
        ItemRequest itemRequest = ItemRequestMapper.itemRequestDtoToItem(itemDto);

        User requester = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id " + userId + " not found"));
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest createdItemRequest = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.itemToResponseDto(createdItemRequest, null);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestResponseDto get(long itemRequestId, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(() ->
                new NotFoundException("ItemRequest with id " + itemRequestId + " not found"));
        return ItemRequestMapper.itemToResponseDto(itemRequest, getItemsForRequest(itemRequestId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponseDto> getItemRequestsByUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        return convertToDtoWithItems(itemRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponseDto> getOtherUsersRequests(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId);

        return convertToDtoWithItems(itemRequests);
    }

    private List<Item> getItemsForRequest(long requestId) {
        return itemRepository.findAllByRequestId(requestId);
    }

    private List<ItemRequestResponseDto> convertToDtoWithItems(List<ItemRequest> itemRequests) {
        if (itemRequests.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> requestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .toList();

        List<Item> items = itemRepository.findAllByRequestIdIn(requestIds);

        Map<Long, List<Item>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return itemRequests.stream()
                .map(itemRequest -> ItemRequestMapper.itemToResponseDto(
                        itemRequest,
                        itemsByRequestId.getOrDefault(itemRequest.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }
}