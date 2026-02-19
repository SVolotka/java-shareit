package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody BookItemRequestDto bookingRequestDto,
                                         @RequestHeader("X-Sharer-User-Id") long bookerId) {
        log.info("Gateway: Create booking request received: {}", bookingRequestDto);
        ResponseEntity<Object> createdBooking = bookingClient.create(bookerId, bookingRequestDto);
        log.info("Gateway: Booking created successfully");
        return createdBooking;
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveOrRejectBooking(@PathVariable long bookingId,
                                                         @RequestParam boolean approved,
                                                         @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Gateway: Approved booking request received: id = {}", bookingId);
        ResponseEntity<Object> approvedBooking = bookingClient.approveOrRejectBooking(bookingId, approved, ownerId);
        log.info("Gateway: Booking approved successfully");
        return approvedBooking;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Gateway: Get booking request received: id = {}", bookingId);
        ResponseEntity<Object> existingBooking = bookingClient.getBooking(bookingId, userId);
        log.info("Gateway: Booking founded successfully");
        return existingBooking;
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByBooker(@RequestParam(defaultValue = "ALL") BookingState state,
                                                      @RequestHeader("X-Sharer-User-Id") long bookerId) {
        log.info("Gateway: Get bookings by booker request received: bookerId = {}", bookerId);
        ResponseEntity<Object> foundedBookings = bookingClient.getBookingsByBooker(state, bookerId);
        log.info("Gateway: Bookings by booker founded successfully");
        return foundedBookings;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestParam(defaultValue = "ALL") BookingState state,
                                                   @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Gateway: Get bookings by owner request received: ownerId = {}", ownerId);
        ResponseEntity<Object> foundedBookings = bookingClient.getOwnerBookings(state, ownerId);
        log.info("Gateway: Bookings by owner founded successfully");
        return foundedBookings;
    }
}
