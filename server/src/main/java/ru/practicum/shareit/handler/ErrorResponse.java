package ru.practicum.shareit.handler;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private Integer error;
    private String description;
}
