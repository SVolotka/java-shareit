package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequestDto {
    @NotNull
    private long itemId;
    @Future
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
}
