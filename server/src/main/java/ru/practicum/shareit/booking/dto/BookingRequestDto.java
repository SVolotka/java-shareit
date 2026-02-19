package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequestDto {
    private Long itemId;
    @FutureOrPresent(message = "Start date must be present or in the future")
    private LocalDateTime start;

    @Future(message = "End date must be in the future")
    private LocalDateTime end;
}
