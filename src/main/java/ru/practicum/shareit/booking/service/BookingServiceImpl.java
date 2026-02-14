package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponseDto create(BookingRequestDto bookingRequestDto, long bookerId) {
        User existingBooker = userRepository.findById(bookerId).orElseThrow(() ->
                new NotFoundException("User with id " + bookerId + " not found"));
        Item existingItem = itemRepository.findById(bookingRequestDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Item with id " + bookingRequestDto.getItemId() + " not found"));

        if (existingItem.getOwner().getId().equals(bookerId)) {
            throw new ValidationException("Owner cannot book his own item");
        }

        if (!existingItem.getAvailable()) {
            throw new ValidationException("Item it is not available for booking");
        }

        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd()) ||
                bookingRequestDto.getStart().isEqual(bookingRequestDto.getEnd())) {
            throw new ValidationException("Start date must be before end date");
        }

        Booking booking = BookingMapper.requestDtoToBooking(bookingRequestDto);
        booking.setBooker(existingBooker);
        booking.setItem(existingItem);
        booking.setStatus(BookingStatus.WAITING);

        Booking createdBooking = bookingRepository.save(booking);
        return BookingMapper.bookingToResponseDto(createdBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto approveOrRejectBooking(long bookingId, long ownerId, boolean approve) {
        Booking existingBooking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Booking with id " + bookingId + " not found"));
        Item item = existingBooking.getItem();

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new ValidationException("Only the owner of this item can approve the booking");
        }

        BookingStatus status = approve ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        existingBooking.setStatus(status);

        Booking updatedBooking = bookingRepository.save(existingBooking);
        return BookingMapper.bookingToResponseDto(updatedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto get(long bookingId, long userId) {
        Booking existingBooking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Booking with id " + bookingId + " not found"));

        if ((!existingBooking.getBooker().getId().equals(userId)) &&
                (!existingBooking.getItem().getOwner().getId().equals(userId))) {
            throw new ValidationException(
                    "Only the owner of this item or the person who made the booking can view this booking.");
        }
        return BookingMapper.bookingToResponseDto(existingBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllBookingsByBooker(long bookerId, BookingState bookingState) {
        userRepository.findById(bookerId).orElseThrow(() ->
                new NotFoundException("User with id " + bookerId + " not found"));

        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findAllByBookerId(bookerId, sort);
            case CURRENT -> bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(bookerId, now, now, sort);
            case PAST -> bookingRepository.findByBookerIdAndEndIsBefore(bookerId, now, sort);
            case FUTURE -> bookingRepository.findByBookerIdAndStartIsAfter(bookerId, now, sort);
            case WAITING -> bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.WAITING, sort);
            case REJECTED -> bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, sort);
        };

        return bookings.stream()
                .map(BookingMapper::bookingToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllBookingsByOwner(long ownerId, BookingState bookingState) {
        userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException("User with id " + ownerId + " not found"));


        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findAllByItemOwnerId(ownerId, sort);
            case CURRENT -> bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(ownerId, now, now, sort);
            case PAST -> bookingRepository.findByItemOwnerIdAndEndIsBefore(ownerId, now, sort);
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartIsAfter(ownerId, now, sort);
            case WAITING -> bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING, sort);
            case REJECTED -> bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, sort);
        };

        return bookings.stream()
                .map(BookingMapper::bookingToResponseDto)
                .collect(Collectors.toList());
    }
}