package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;

public class ItemRequestDto {
    @NotBlank(message = "Description cannot be blank")
    private String description;
}
