package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Valid ItemDto itemDto) {
        log.info("Gateway: Create item request received: {}", itemDto);
        ResponseEntity<Object> createdItem = itemClient.create(userId, itemDto);
        log.info("Gateway: Item created successfully");
        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody ItemDto itemDto) {
        log.info("Gateway: Update item request received: id = {}", itemId);
        log.debug("Gateway: Update item details: {}", itemDto);
        ResponseEntity<Object> updatedItem = itemClient.update(itemId, userId, itemDto);
        log.info("Gateway: Item updated successfully");
        return updatedItem;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Gateway: Get item request received: id = {}", itemId);
        ResponseEntity<Object> existingItem = itemClient.get(itemId, userId);
        log.info("Gateway: Item founded successfully");
        return existingItem;
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Gateway: Get items by user request received: userId = {}", userId);
        ResponseEntity<Object> foundedItems = itemClient.getItemsByUser(userId);
        log.info("Gateway: Items founded successfully");
        return foundedItems;
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> delete(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Gateway: Delete item request received: id = {}", itemId);
        ResponseEntity<Object> deletedItem = itemClient.delete(itemId, userId);
        log.info("Gateway: Item deleted successfully");
        return deletedItem;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(value = "text", required = false) String searchText) {
        log.info("Gateway: Search request received: text = {}", searchText);
        ResponseEntity<Object> items = itemClient.search(searchText);
        log.info("Gateway: Search completed successfully");
        return items;
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @PathVariable Long itemId,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestBody CommentDto commentDto) {
        log.info("Gateway: Create comment request received: {}", commentDto);
        ResponseEntity<Object> createdComment = itemClient.createComment(itemId, userId, commentDto);
        log.info("Gateway: Comment created successfully");
        return createdComment;
    }
}