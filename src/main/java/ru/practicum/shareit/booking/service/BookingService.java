package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto create(BookingRequestDto bookingRequestDto, long bookerId);

    BookingResponseDto approveOrRejectBooking(long bookingId, long ownerId, boolean approve);

    BookingResponseDto get(long bookingId, long userId);

    List<BookingResponseDto> getAllBookingsByBooker(long bookerId, BookingState bookingState);

    List<BookingResponseDto> getAllBookingsByOwner(long ownerId, BookingState bookingState);
}
