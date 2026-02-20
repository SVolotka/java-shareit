package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Gateway: Create ItemRequest request received: {}", itemRequestDto);
        ResponseEntity<Object> createdItemRequest = itemRequestClient.create(userId, itemRequestDto);
        log.info("Gateway: ItemRequest created successfully");
        return createdItemRequest;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> get(@PathVariable long requestId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Gateway: Get itemRequest request received: id = {}", requestId);
        ResponseEntity<Object> existingItemRequest = itemRequestClient.get(requestId, userId);
        log.info("Gateway: ItemRequest found successfully");
        return existingItemRequest;
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Gateway: Get ItemRequests by user request received: userId = {}", userId);
        ResponseEntity<Object> retrievedItemRequests = itemRequestClient.getItemRequestsByUser(userId);
        log.info("Gateway: ItemRequests found successfully");
        return retrievedItemRequests;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Gateway: Received request to get item requests of other users. Current userId: {}", userId);
        ResponseEntity<Object> retrievedItemRequests = itemRequestClient.getOtherUsersRequests(userId);
        log.info("Gateway: Successfully retrieved item requests for other users. Current userId: {}", userId);
        return retrievedItemRequests;
    }
}