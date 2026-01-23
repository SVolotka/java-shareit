package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
ItemDto create(ItemDto itemDto, long userId);

ItemDto update(ItemDto itemDto, long itemId, long userId);

ItemDto get(long itemId);

List<ItemDto> getItemsByUser(long userId);

void delete(long itemId, long userId);

List<ItemDto> searchItems(String searchText);
}