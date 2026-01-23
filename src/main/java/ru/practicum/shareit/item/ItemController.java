package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("Create item request received: {}", itemDto);
        ItemDto createdItem = itemService.create(itemDto, userId);
        log.info("Item created successfully");
        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody ItemDto itemDto) {
        log.info("Update item request received: id = {}", itemId);
        log.debug("Update item details: {}", itemDto);
        ItemDto updatedItem = itemService.update(itemDto, itemId, userId);
        log.info("Item updated successfully");
        return updatedItem;
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable long itemId) {
        log.info("Get item request received: id = {}", itemId);
        ItemDto existingItem = itemService.get(itemId);
        log.info("Item founded successfully");
        return existingItem;
    }

    @GetMapping
    public List<ItemDto> getItemsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get items by user request received: userId = {}", userId);
        List<ItemDto> foundedItems = itemService.getItemsByUser(userId);
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
    public List<ItemDto> search(@RequestParam(value = "text", required = false) String searchText) {
        log.info("Search request received: text = {}", searchText);
        List<ItemDto> items = itemService.searchItems(searchText);
        log.info("Search completed successfully");
        return items;
    }
}