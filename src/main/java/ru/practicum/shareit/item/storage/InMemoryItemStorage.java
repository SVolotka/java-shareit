package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private long counter = 1;

    Map<Long, Item> items = new HashMap<>();
    private final InMemoryUserStorage inMemoryUserStorage;

    @Override
    public Item create(Item item, long userId) {
        item.setId(generatedId());
        User owner = inMemoryUserStorage.get(userId);

        item.setOwner(owner);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item, long itemId, long userId) {
        Item existingItem = get(itemId);
        inMemoryUserStorage.get(userId);

        if (existingItem.getOwner().getId() != userId) {
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
        return existingItem;
    }

    @Override
    public Item get(long id) {
        if (items.containsKey(id)) {
            return items.get(id);
        }
        throw new ItemNotFoundException("Item with id " + id + " not found");
    }

    @Override
    public List<Item> getAllByUser(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> find(String searchText) {
        if (searchText == null || searchText.isBlank()) {
            return List.of();
        }

        String lowerCaseSearchText = searchText.toLowerCase();

        return items.values().stream()
                .filter(item -> (item.getName() != null && item.getName().toLowerCase()
                        .contains(lowerCaseSearchText)) ||
                        (item.getDescription() != null && item.getDescription().toLowerCase()
                                .contains(lowerCaseSearchText))
                )
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long itemId, long userId) {
        Item existingItem = get(itemId);

        if (existingItem.getOwner().getId() != userId) {
            throw new NotOwnerException("Only item owner can delete it");
        }
        items.remove(existingItem);
    }

    private long generatedId() {
        return counter++;
    }
}