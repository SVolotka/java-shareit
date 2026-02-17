package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

public class BookingMapper {
    public static BookingResponseDto bookingToResponseDto(Booking booking) {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setStart(booking.getStart());
        bookingResponseDto.setEnd(booking.getEnd());
        bookingResponseDto.setItem(ItemMapper.itemToResponseDtoWithOutComments(booking.getItem()));
        bookingResponseDto.setBooker(UserMapper.userToDto(booking.getBooker()));
        bookingResponseDto.setStatus(booking.getStatus());

        return bookingResponseDto;
    }

    public static Booking requestDtoToBooking(BookingRequestDto bookingRequestDto) {
        Booking booking = new Booking();
        booking.setStart(bookingRequestDto.getStart());
        booking.setEnd(bookingRequestDto.getEnd());

        return booking;
    }
}
