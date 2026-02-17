package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto create(@Valid @RequestBody BookingRequestDto bookingRequestDto,
                                            @RequestHeader("X-Sharer-User-Id") long bookerId) {
        log.info("Create booking request received: {}", bookingRequestDto);
        BookingResponseDto createdBooking = bookingService.create(bookingRequestDto, bookerId);
        log.info("Booking created successfully");
        return createdBooking;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveOrRejectBooking(@PathVariable long bookingId,
                                             @RequestParam boolean approved,
                                             @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Approved booking request received: id = {}", bookingId);
        BookingResponseDto approvedBooking = bookingService.approveOrRejectBooking(bookingId, ownerId, approved);
        log.info("Booking approved successfully");
        return approvedBooking;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@PathVariable long bookingId,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get booking request received: id = {}", bookingId);
        BookingResponseDto existingBooking = bookingService.get(bookingId, userId);
        log.info("Booking founded successfully");
        return existingBooking;
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsByBooker(@RequestParam(defaultValue = "ALL") BookingState state,
                                                    @RequestHeader("X-Sharer-User-Id") long bookerId) {
        log.info("Get bookings by booker request received: bookerId = {}", bookerId);
        List<BookingResponseDto> foundedBookings = bookingService.getAllBookingsByBooker(bookerId, state);
        log.info("Bookings by booker founded successfully");
        return foundedBookings;
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(@RequestParam(defaultValue = "ALL") BookingState state,
                                                     @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Get bookings by owner request received: ownerId = {}", ownerId);
        List<BookingResponseDto> foundedBookings = bookingService.getAllBookingsByOwner(ownerId, state);
        log.info("Bookings by owner founded successfully");
        return foundedBookings;
    }
}
