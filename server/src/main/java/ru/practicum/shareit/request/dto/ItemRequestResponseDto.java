package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoForItemRequest;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestResponseDto {
    private Long id;
    private String description;
    private LocalDateTime created;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private List<ItemDtoForItemRequest> items;
}