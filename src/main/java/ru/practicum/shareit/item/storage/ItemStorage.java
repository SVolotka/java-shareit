package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item create(Item item, long userId);

    Item update(Item item,long itemId, long userId);

    Item get(long itemId);

    List<Item> getAllByUser(long userId);

    List<Item> find(String searchText);

    void delete(long itemId, long userId);
}
