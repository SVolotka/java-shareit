package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class CommentDto {
    private Long id;
    @NotBlank(message = "Comment cannot be blank")
    private String text;
    private Long itemId;
    private String authorName;
    private LocalDateTime created;
}
