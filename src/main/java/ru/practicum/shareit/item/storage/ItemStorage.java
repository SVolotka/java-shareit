package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item create(Item item);

    Item update(Item item,long itemId);

    Optional<Item> get(long itemId);

    List<Item> getAllByUser(long userId);

    List<Item> find(String searchText);

    void delete(long itemId, long userId);
}
