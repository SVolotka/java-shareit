package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemResponseDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Create item request received: {}", itemRequestDto);
        ItemResponseDto createdItem = itemService.create(itemRequestDto, userId);
        log.info("Item created successfully");
        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto update(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId,
                                  @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Update item request received: id = {}", itemId);
        log.debug("Update item details: {}", itemRequestDto);
        ItemResponseDto updatedItem = itemService.update(itemRequestDto, itemId, userId);
        log.info("Item updated successfully");
        return updatedItem;
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto get(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get item request received: id = {}", itemId);
        ItemResponseDto existingItem = itemService.get(itemId, userId);
        log.info("Item founded successfully");
        return existingItem;
    }

    @GetMapping
    public List<ItemResponseDto> getItemsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get items by user request received: userId = {}", userId);
        List<ItemResponseDto> foundedItems = itemService.getItemsByUser(userId);
        log.info("Items founded successfully");
        return foundedItems;
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Delete item request received: id = {}", itemId);
        itemService.delete(itemId, userId);
        log.info("Item deleted successfully");
    }

    @GetMapping("/search")
    public List<ItemResponseDto> search(@RequestParam(value = "text", required = false) String searchText) {
        log.info("Search request received: text = {}", searchText);
        List<ItemResponseDto> items = itemService.searchItems(searchText);
        log.info("Search completed successfully");
        return items;
    }

    @PostMapping("{itemId}/comment")
    public CommentDto createComment(
            @PathVariable Long itemId,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @RequestBody CommentDto commentDto) {
        log.info("Create comment request received: {}", commentDto);
        CommentDto createdComment = itemService.createComment(commentDto, itemId, userId);
        log.info("Comment created successfully");
        return createdComment;
    }
}