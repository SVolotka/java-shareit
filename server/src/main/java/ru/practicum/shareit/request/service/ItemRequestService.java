package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponseDto create(ItemRequestDto itemDto, long userId);

    ItemRequestResponseDto get(long itemRequestId, long userId);

    List<ItemRequestResponseDto> getItemRequestsByUser(long userId);

    List<ItemRequestResponseDto> getOtherUsersRequests(long userId);
}
