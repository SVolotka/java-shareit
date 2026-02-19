package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForItemRequest;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {
    public static ItemResponseDto itemToResponseDtoWithOutComments(Item item) {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(item.getId());
        itemResponseDto.setName(item.getName());
        itemResponseDto.setDescription(item.getDescription());
        itemResponseDto.setAvailable(item.getAvailable());
        itemResponseDto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        return itemResponseDto;
    }

    public static Item requestDtoToItem(ItemDto requestDto) {
        Item item = new Item();
        item.setName(requestDto.getName());
        item.setDescription(requestDto.getDescription());
        item.setAvailable(requestDto.getAvailable());
        return item;
    }

    public static ItemResponseDto itemToResponseDto(Item item, List<CommentDto> comments,
                                                                    BookingResponseDto lastBooking, BookingResponseDto nextBooking) {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(item.getId());
        itemResponseDto.setName(item.getName());
        itemResponseDto.setDescription(item.getDescription());
        itemResponseDto.setAvailable(item.getAvailable());
        itemResponseDto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        itemResponseDto.setComments(comments);
        itemResponseDto.setLastBooking(lastBooking);
        itemResponseDto.setNextBooking(nextBooking);
        return itemResponseDto;
    }

    public static ItemDtoForItemRequest itemToDtoForItemRequest(Item item) {
        ItemDtoForItemRequest dtoForItemRequest = new ItemDtoForItemRequest();
        dtoForItemRequest.setId(item.getId());
        dtoForItemRequest.setName(item.getName());
        dtoForItemRequest.setOwnerId(item.getOwner().getId());
        return dtoForItemRequest;
    }
}