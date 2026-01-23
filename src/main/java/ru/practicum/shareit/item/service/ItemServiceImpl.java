package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final InMemoryItemStorage inMemoryItemStorage;

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        Item createdItem = inMemoryItemStorage.create(ItemMapper.dtoToItem(itemDto), userId);
        return ItemMapper.itemToDto(createdItem);
    }

    @Override
    public ItemDto update(ItemDto itemDto, long itemId, long userId) {
        Item item = ItemMapper.dtoToItem(itemDto);
        Item existingItem = inMemoryItemStorage.update(item, itemId, userId);
        return ItemMapper.itemToDto(existingItem);
    }

    @Override
    public ItemDto get(long itemId) {
        Item existingItem = inMemoryItemStorage.get(itemId);
        return ItemMapper.itemToDto(existingItem);
    }

    @Override
    public List<ItemDto> getItemsByUser(long userId) {
        List<Item> items = inMemoryItemStorage.getAllByUser(userId);

        return items.stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long itemId, long userId) {
        inMemoryItemStorage.delete(itemId, userId);
    }

    @Override
    public List<ItemDto> searchItems(String searchText) {
        List<Item> items = inMemoryItemStorage.find(searchText);
        return items.stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }
}