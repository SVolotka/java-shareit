package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemResponseDto create(ItemRequestDto itemRequestDto, long userId) {
        Item item = ItemMapper.requestDtoToItem(itemRequestDto);
        User owner = userStorage.get(userId).orElseThrow(() ->
                new NotFoundException("User with id " + userId + " not found"));

        item.setOwner(owner);
        Item createdItem = itemStorage.create(item);
        return ItemMapper.itemToResponseDto(createdItem);
    }

    @Override
    public ItemResponseDto update(ItemRequestDto itemRequestDtoDto, long itemId, long userId) {
        Item item = ItemMapper.requestDtoToItem(itemRequestDtoDto);

        Item existingItem = itemStorage.get(itemId).orElseThrow(() ->
                new NotFoundException("Item with id " + itemId + " not found"));
        userStorage.get(userId);

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

        Item updatedItem = itemStorage.update(existingItem, itemId);
        return ItemMapper.itemToResponseDto(updatedItem);
    }

    @Override
    public ItemResponseDto get(long itemId) {
        Item existingItem = itemStorage.get(itemId).orElseThrow(() ->
                new NotFoundException("Item with id " + itemId + " not found"));

        return ItemMapper.itemToResponseDto(existingItem);
    }

    @Override
    public List<ItemResponseDto> getItemsByUser(long userId) {
        List<Item> items = itemStorage.getAllByUser(userId);

        return items.stream()
                .map(ItemMapper::itemToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long itemId, long userId) {
        Item existingItem = itemStorage.get(itemId).orElseThrow(() ->
                new NotFoundException("Item with id " + itemId + " not found"));

        if (existingItem.getOwner().getId() != userId) {
            throw new NotOwnerException("Only item owner can delete it");
        }
        itemStorage.delete(itemId, userId);
    }

    @Override
    public List<ItemResponseDto> searchItems(String searchText) {
        if (searchText == null || searchText.isBlank()) {
            return List.of();
        }
        List<Item> items = itemStorage.find(searchText);
        return items.stream()
                .map(ItemMapper::itemToResponseDto)
                .collect(Collectors.toList());
    }
}