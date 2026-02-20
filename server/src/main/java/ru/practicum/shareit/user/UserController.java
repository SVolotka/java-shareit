package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userServiceImpl;

    @PostMapping
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        log.info("Create user request received: {}", userDto);
        UserDto createdUser = userServiceImpl.create(userDto);
        log.info("User created successfully");
        return createdUser;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("Update user request received: id = {}", userId);
        log.debug("Update user details: {}", userDto);
        UserDto updatedUser = userServiceImpl.update(userDto, userId);
        log.info("User updated successfully");
        return updatedUser;
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable long userId) {
        log.info("Get user request received: id = {}", userId);
        UserDto existingUser = userServiceImpl.get(userId);
        log.info("User founded successfully");
        return existingUser;
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Get all users request received");
        List<UserDto> allUsers = userServiceImpl.getAll();
        log.info("All users founded successfully");
        return allUsers;
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Delete user request received: id = {}", userId);
        userServiceImpl.delete(userId);
        log.info("User deleted successfully");
    }
}