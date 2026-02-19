package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDateTime;

public class BookItemRequestDto {
    private long itemId;

    @FutureOrPresent(message = "Start date must be present or in the future")
    private LocalDateTime start;

    @Future(message = "End date must be in the future")
    private LocalDateTime end;
}
