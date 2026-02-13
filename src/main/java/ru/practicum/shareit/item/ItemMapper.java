package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemResponseDto itemToResponseDto(Item item) {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(item.getId());
        itemResponseDto.setName(item.getName());
        itemResponseDto.setDescription(item.getDescription());
        itemResponseDto.setAvailable(item.getAvailable());
        itemResponseDto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        return itemResponseDto;

    }

    public static Item requestDtoToItem(ItemRequestDto requestDto) {
        Item item = new Item();
        item.setName(requestDto.getName());
        item.setDescription(requestDto.getDescription());
        item.setAvailable(requestDto.getAvailable());
        return item;
    }
}