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
import java.util.List;
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
            throw  new NotFoundException("User with id " + userId + " not found");
        }
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(() ->
                new NotFoundException("ItemRequest with id " + itemRequestId + " not found"));
        return ItemRequestMapper.itemToResponseDto(itemRequest, getItemsForRequest(itemRequestId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponseDto> getItemRequestsByUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw  new NotFoundException("User with id " + userId + " not found");
        }

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        return itemRequests.stream()
                .map(itemRequest -> ItemRequestMapper.itemToResponseDto(
                        itemRequest, getItemsForRequest(itemRequest.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponseDto> getOtherUsersRequests(long userId) {
        if (!userRepository.existsById(userId)) {
            throw  new NotFoundException("User with id " + userId + " not found");
        }

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId);

        return itemRequests.stream()
                .map(itemRequest -> ItemRequestMapper.itemToResponseDto(
                        itemRequest, getItemsForRequest(itemRequest.getId())))
                .collect(Collectors.toList());
    }

    private List<Item> getItemsForRequest(long requestId) {
        return itemRepository.findAllByRequestId(requestId);
    }
}
