package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid UserDto userDto) {
        log.info("Gateway: Create user request received: {}", userDto);
        ResponseEntity<Object> createdUser = userClient.create(userDto);
        log.info("Gateway: User created successfully");
        return createdUser;
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("Gateway: Update user request received: id = {}", userId);
        log.debug("Gateway: Update user details: {}", userDto);
        ResponseEntity<Object> updatedUser = userClient.update(userDto, userId);
        log.info("Gateway: User updated successfully");
        return updatedUser;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable long userId) {
        log.info("Gateway: Get user request received: id = {}", userId);
        ResponseEntity<Object> existingUser = userClient.get(userId);
        log.info("Gateway: User found successfully");
        return existingUser;
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Gateway: Get all users request received");
        ResponseEntity<Object> allUsers = userClient.getAll();
        log.info("Gateway: All users found successfully");
        return allUsers;
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable long userId) {
        log.info("Gateway: Delete user request received: id = {}", userId);
        ResponseEntity<Object> deletedUser = userClient.delete(userId);
        log.info("Gateway: User deleted successfully");
        return deletedUser;
    }
}