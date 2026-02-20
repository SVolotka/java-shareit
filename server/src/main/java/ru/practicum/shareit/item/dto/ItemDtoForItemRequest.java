package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

@Data
public class ItemDtoForItemRequest {
    private Long id;
    private String name;
    private Long ownerId;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private BookingResponseDto nextBooking;
}
