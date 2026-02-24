package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    @NotBlank(message = "Comment cannot be blank")
    private String text;
    private Long itemId;
    private String authorName;
    private LocalDateTime created;
}
