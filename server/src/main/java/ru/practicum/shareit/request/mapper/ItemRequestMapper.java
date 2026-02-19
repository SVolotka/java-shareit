package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequest itemRequestDtoToItem(ItemRequestDto requestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(requestDto.getDescription());
        return itemRequest;
    }

    public static ItemRequestResponseDto itemToResponseDto(ItemRequest itemRequest, List<Item> items) {
        ItemRequestResponseDto responseDto = new ItemRequestResponseDto();

        responseDto.setId(itemRequest.getId());
        responseDto.setDescription(itemRequest.getDescription());
        responseDto.setCreated(itemRequest.getCreated());

        if (items != null) {
            responseDto.setItems(items.stream()
                    .map(ItemMapper::itemToDtoForItemRequest)
                    .collect(Collectors.toList()));
        } else {
            responseDto.setItems(Collections.emptyList());
        }
        return responseDto;
    }
}