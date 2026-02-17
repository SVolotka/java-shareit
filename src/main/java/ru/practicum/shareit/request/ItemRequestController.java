package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Create ItemRequest request received: {}", itemRequestDto);
        ItemRequestResponseDto createdItemRequest = itemRequestService.create(itemRequestDto, userId);
        log.info("ItemRequest created successfully");
        return createdItemRequest;
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto get(@PathVariable long requestId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get itemRequest request received: id = {}", requestId);
        ItemRequestResponseDto existingItemRequest = itemRequestService.get(requestId, userId);
        return existingItemRequest;
    }

    @GetMapping
    public List<ItemRequestResponseDto> getItemRequestsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get ItemRequests by user request received: userId = {}", userId);
        List<ItemRequestResponseDto> retrievedItemRequests = itemRequestService.getItemRequestsByUser(userId);
        log.info("ItemRequests founded successfully");
        return retrievedItemRequests;
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getOtherUsersRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Received request to get item requests of other users. Current userId: {}", userId);
        List<ItemRequestResponseDto> retrievedItemRequests = itemRequestService.getOtherUsersRequests(userId);
        log.info("Successfully retrieved item requests for other users. Current userId: {}", userId);
        return retrievedItemRequests;
    }
}
