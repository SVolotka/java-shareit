package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForItemRequest;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemMapperTest {

    @Test
    void itemToResponseDtoWithOutComments() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("John");
        owner.setEmail("john.doe@mail.com");

        Item item = new Item();
        item.setId(10L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(null);

        ItemResponseDto dto = ItemMapper.itemToResponseDtoWithOutComments(item);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(10L, dto.getId());
        Assertions.assertEquals("Item", dto.getName());
        Assertions.assertEquals("Description", dto.getDescription());
        Assertions.assertTrue(dto.getAvailable());
        Assertions.assertNull(dto.getRequestId());
        Assertions.assertNull(dto.getComments());
        Assertions.assertNull(dto.getLastBooking());
        Assertions.assertNull(dto.getNextBooking());
    }

    @Test
    void requestDtoToItem() {
        ItemDto requestDto = new ItemDto();
        requestDto.setName("Item");
        requestDto.setDescription("Description");
        requestDto.setAvailable(true);
        requestDto.setRequestId(1L);

        Item item = ItemMapper.requestDtoToItem(requestDto);

        Assertions.assertNotNull(item);
        Assertions.assertEquals("Item", item.getName());
        Assertions.assertEquals("Description", item.getDescription());
        Assertions.assertTrue(item.getAvailable());
    }

    @Test
    void itemToResponseDto() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("John");
        owner.setEmail("john.doe@mail.com");

        ItemRequest request = new ItemRequest();
        request.setId(10L);

        Item item = new Item();
        item.setId(100L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);


        CommentDto firstComment = new CommentDto();
        firstComment.setId(1L);
        firstComment.setText("Great!");
        firstComment.setAuthorName("Yaroslav");
        firstComment.setCreated(LocalDateTime.now());

        CommentDto secondComment = new CommentDto();
        secondComment.setId(2L);
        secondComment.setText("Not bad");
        secondComment.setAuthorName("Xenia");
        secondComment.setCreated(LocalDateTime.now());

        List<CommentDto> comments = List.of(firstComment, secondComment);

        BookingResponseDto lastBooking = new BookingResponseDto();
        lastBooking.setId(100L);
        lastBooking.setStart(LocalDateTime.now().minusDays(5));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1));

        BookingResponseDto nextBooking = new BookingResponseDto();
        nextBooking.setId(101L);
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        nextBooking.setEnd(LocalDateTime.now().plusDays(3));

        ItemResponseDto dto = ItemMapper.itemToResponseDto(item, comments, lastBooking, nextBooking);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(100L, dto.getId());
        Assertions.assertEquals("Item", dto.getName());
        Assertions.assertEquals("Description", dto.getDescription());
        Assertions.assertTrue(dto.getAvailable());
        Assertions.assertEquals(10L, dto.getRequestId());

        Assertions.assertNotNull(dto.getComments());
        Assertions.assertEquals(2, dto.getComments().size());
        Assertions.assertEquals(firstComment, dto.getComments().get(0));
        Assertions.assertEquals(secondComment, dto.getComments().get(1));

        Assertions.assertNotNull(dto.getLastBooking());
        Assertions.assertEquals(100L, dto.getLastBooking().getId());
        Assertions.assertNotNull(dto.getNextBooking());
        Assertions.assertEquals(101L, dto.getNextBooking().getId());
    }

    @Test
    void itemToDtoForItemRequest() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("John");

        Item item = new Item();
        item.setId(10L);
        item.setName("Item");
        item.setOwner(owner);

        ItemDtoForItemRequest dto = ItemMapper.itemToDtoForItemRequest(item);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(10L, dto.getId());
        Assertions.assertEquals("Item", dto.getName());
        Assertions.assertEquals(1L, dto.getOwnerId());
    }
}