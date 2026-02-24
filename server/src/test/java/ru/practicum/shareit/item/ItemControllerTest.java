package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.exception.NotOwnerException;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private ItemResponseDto itemResponseDto;
    private CommentDto commentDto;
    private CommentDto createdCommentDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(1L);
        itemResponseDto.setName("Drill");
        itemResponseDto.setDescription("Powerful drill");
        itemResponseDto.setAvailable(true);
        itemResponseDto.setRequestId(1L);

        commentDto = new CommentDto();
        commentDto.setText("Great item!");

        createdCommentDto = new CommentDto();
        createdCommentDto.setId(10L);
        createdCommentDto.setText("Great item!");
        createdCommentDto.setAuthorName("John");
        createdCommentDto.setCreated(LocalDateTime.now());
    }

    @Test
    void createItem_shouldReturnCreatedItem() throws Exception {
        when(itemService.create(any(ItemDto.class), eq(1L))).thenReturn(itemResponseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.description").value("Powerful drill"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.requestId").value(1L));
    }

    @Test
    void createItem_withInvalidData_shouldReturnBadRequest() throws Exception {
        itemDto.setName(""); // невалидно

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        when(itemService.update(any(ItemDto.class), eq(1L), eq(1L))).thenReturn(itemResponseDto);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void updateItem_byNonOwner_shouldReturnForbidden() throws Exception {
        when(itemService.update(any(ItemDto.class), eq(1L), eq(2L)))
                .thenThrow(new NotOwnerException("Only item owner can modify it"));

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void getItem_shouldReturnItem() throws Exception {
        when(itemService.get(1L, 1L)).thenReturn(itemResponseDto);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void getItemsByUser_shouldReturnList() throws Exception {
        List<ItemResponseDto> items = List.of(itemResponseDto);
        when(itemService.getItemsByUser(1L)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void deleteItem_shouldReturnOk() throws Exception {
        doNothing().when(itemService).delete(1L, 1L);

        mockMvc.perform(delete("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void searchItems_shouldReturnList() throws Exception {
        List<ItemResponseDto> items = List.of(itemResponseDto);
        when(itemService.searchItems("drill")).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void searchItems_withBlankText_shouldReturnEmptyList() throws Exception {
        when(itemService.searchItems("   ")).thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .param("text", "   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void createComment_shouldReturnCreatedComment() throws Exception {
        when(itemService.createComment(any(CommentDto.class), eq(1L), eq(1L))).thenReturn(createdCommentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.text").value("Great item!"))
                .andExpect(jsonPath("$.authorName").value("John"));
    }
}