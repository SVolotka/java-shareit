package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {
    ItemResponseDto create(ItemRequestDto itemRequestDto, long userId);

    ItemResponseDto update(ItemRequestDto itemRequestDto, long itemId, long userId);

    ItemResponseDto get(long itemId, long userId);

    List<ItemResponseDto> getItemsByUser(long userId);

    void delete(long itemId, long userId);

    List<ItemResponseDto> searchItems(String searchText);

    CommentDto createComment(CommentDto commentDto, long itemId, long userId);
}