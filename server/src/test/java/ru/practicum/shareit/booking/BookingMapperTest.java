package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class BookingMapperTest {

    @Test
    void bookingToResponseDto() {
        User booker = new User();
        booker.setId(1L);
        booker.setName("John");
        booker.setEmail("john.doe@mail.com");

        Item item = new Item();
        item.setId(10L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);

        Booking booking = new Booking();
        booking.setId(100L);
        booking.setStart(LocalDateTime.of(2026, 1, 1, 0, 0));
        booking.setEnd(LocalDateTime.of(2026, 1, 2, 0, 0));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        BookingResponseDto responseDto = BookingMapper.bookingToResponseDto(booking);

        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(100L, responseDto.getId());
        Assertions.assertEquals(booking.getStart(), responseDto.getStart());
        Assertions.assertEquals(booking.getEnd(), responseDto.getEnd());
        Assertions.assertEquals(BookingStatus.WAITING, responseDto.getStatus());
        Assertions.assertNotNull(responseDto.getBooker());
        Assertions.assertNotNull(responseDto.getItem());
    }


    @Test
    void requestDtoToBooking() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setStart(LocalDateTime.of(2026, 1, 1, 0, 0));
        requestDto.setEnd(LocalDateTime.of(2026, 1, 2, 0, 0));

        Booking booking = BookingMapper.requestDtoToBooking(requestDto);

        Assertions.assertNotNull(booking);
        Assertions.assertEquals(requestDto.getStart(), booking.getStart());
        Assertions.assertEquals(requestDto.getEnd(), booking.getEnd());
    }
}