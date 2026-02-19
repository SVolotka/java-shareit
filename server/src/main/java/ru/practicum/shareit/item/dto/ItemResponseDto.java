package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

@Data
public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private List<CommentDto> comments;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private BookingResponseDto lastBooking;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private BookingResponseDto nextBooking;
}
