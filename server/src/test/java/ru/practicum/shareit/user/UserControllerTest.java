package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    UserDto userToSave = new UserDto();
    UserDto savedUser = new UserDto();

    @BeforeEach
    void setUp() {
        userToSave.setId(null);
        userToSave.setName("John");
        userToSave.setEmail("john.doe@example.com");

        savedUser.setId(1L);
        savedUser.setName("John");
        savedUser.setEmail("john.doe@example.com");
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        when(userService.create(any(UserDto.class))).thenReturn(savedUser);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userToSave))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void createUser_withInvalidData_shouldReturnBadRequest() throws Exception {
        UserDto invalidDto = new UserDto();
        invalidDto.setName("");
        invalidDto.setEmail("not-an-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        long userId = 1L;
        UserDto updateDto = new UserDto();
        updateDto.setName("Updated Name");
        updateDto.setEmail("updated@example.com");

        UserDto updatedUser = new UserDto();
        updatedUser.setId(userId);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updated@example.com");

        when(userService.update(any(UserDto.class), eq(userId))).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void updateUser_withNonExistingId_shouldReturnNotFound() throws Exception {
        long userId = 999L;
        UserDto updateDto = new UserDto();
        updateDto.setName("Name");

        when(userService.update(any(UserDto.class), eq(userId)))
                .thenThrow(new NotFoundException("User with id 999 not found"));

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUser_shouldReturnUser() throws Exception {

        when(userService.get(1L))
                .thenReturn(savedUser);

        mockMvc.perform(get("/users/{userId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));
    }

    @Test
    void getUser_withNonExistingId_shouldReturnNotFound() throws Exception {
        long userId = 999L;
        when(userService.get(userId)).thenThrow(new NotFoundException("User with id 999 not found"));

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_shouldReturnList() throws Exception {
        List<UserDto> users = List.of(savedUser);
        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void deleteUser_shouldReturnOk() throws Exception {
        long userId = 1L;
        doNothing().when(userService).delete(userId);

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());
    }
}