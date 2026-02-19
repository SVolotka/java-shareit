package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapperTest {
    @Test
    void itemRequestDtoToItem() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need a drill");

        ItemRequest itemRequest = ItemRequestMapper.itemRequestDtoToItem(requestDto);

        Assertions.assertNotNull(itemRequest);
        Assertions.assertEquals("Need a drill", itemRequest.getDescription());
    }

    @Test
    void itemToResponseDto() {
        User requester = new User();
        requester.setId(1L);
        requester.setName("John");
        requester.setEmail("john@mail.com");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(10L);
        itemRequest.setDescription("Need a drill");
        itemRequest.setCreated(LocalDateTime.of(2026, 1, 1, 0, 0));
        itemRequest.setRequester(requester);

        User firstOwner = new User();
        firstOwner.setId(2L);
        firstOwner.setName("First Owner");

        User secondOwner = new User();
        secondOwner.setId(3L);
        secondOwner.setName("Second Owner");

        Item firstItem = new Item();
        firstItem.setId(100L);
        firstItem.setName("Drill");
        firstItem.setOwner(firstOwner);

        Item secondItem = new Item();
        secondItem.setId(101L);
        secondItem.setName("Hammer");
        secondItem.setOwner(secondOwner);

        List<Item> items = List.of(firstItem, secondItem);

        ItemRequestResponseDto responseDto = ItemRequestMapper.itemToResponseDto(itemRequest, items);

        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(10L, responseDto.getId());
        Assertions.assertEquals("Need a drill", responseDto.getDescription());
        Assertions.assertEquals(itemRequest.getCreated(), responseDto.getCreated());

        Assertions.assertNotNull(responseDto.getItems());
        Assertions.assertEquals(2, responseDto.getItems().size());

        Assertions.assertEquals(100L, responseDto.getItems().getFirst().getId());
        Assertions.assertEquals("Drill", responseDto.getItems().getFirst().getName());
        Assertions.assertEquals(2L, responseDto.getItems().getFirst().getOwnerId());

        Assertions.assertEquals(101L, responseDto.getItems().get(1).getId());
        Assertions.assertEquals("Hammer", responseDto.getItems().get(1).getName());
        Assertions.assertEquals(3L, responseDto.getItems().get(1).getOwnerId());
    }

    @Test
    void itemToResponseDto_withoutItems() {
        User requester = new User();
        requester.setId(1L);
        requester.setName("John");
        requester.setEmail("john@mail.com");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(10L);
        itemRequest.setDescription("Need a drill");
        itemRequest.setCreated(LocalDateTime.of(2026, 1, 1, 0, 0));
        itemRequest.setRequester(requester);

        ItemRequestResponseDto responseDto = ItemRequestMapper.itemToResponseDto(itemRequest, null);

        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(10L, responseDto.getId());
        Assertions.assertEquals("Need a drill", responseDto.getDescription());
        Assertions.assertEquals(itemRequest.getCreated(), responseDto.getCreated());

        Assertions.assertNotNull(responseDto.getItems());
        Assertions.assertTrue(responseDto.getItems().isEmpty());
    }
}