package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private long counter = 1;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item create(Item item) {
        item.setId(generatedId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item, long itemId) {
        items.put(itemId, item);
        return item;
    }

    @Override
    public Item get(long id) {
        if (items.containsKey(id)) {
            return items.get(id);
        }
        throw new NotFoundException("Item with id " + id + " not found");
    }

    @Override
    public List<Item> getAllByUser(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> find(String searchText) {
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
        items.remove(itemId);
    }

    private long generatedId() {
        return counter++;
    }
}