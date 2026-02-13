package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto create(UserDto userDto) {
        User newUser = UserMapper.dtoToUser(userDto);
        validateEmail(newUser.getEmail());
        userStorage.create(newUser);

        return UserMapper.userToDto(newUser);
    }

    @Override
    public UserDto update(UserDto userDto, long id) {
        User user = UserMapper.dtoToUser(userDto);
        user.setId(id);

        User existingUser = userStorage.get(user.getId()).orElseThrow(() ->
                new NotFoundException("User with id " + id + " not found"));

        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            validateEmail(user.getEmail());
            existingUser.setEmail(user.getEmail());
        }

        User updatedUser = userStorage.update(existingUser);
        return UserMapper.userToDto(updatedUser);
    }

    @Override
    public UserDto get(long id) {
        User existingUser = userStorage.get(id).orElseThrow(() ->
                new NotFoundException("User with id " + id + " not found"));
        return UserMapper.userToDto(existingUser);
    }

    @Override
    public List<UserDto> getAll() {
        return userStorage.getAll()
                .stream()
                .map(UserMapper::userToDto)
                .toList();
    }

    @Override
    public void delete(long id) {
        userStorage.delete(id);
    }

    private void validateEmail(String email) {
        boolean emailExist = userStorage.getAll().stream()
                .anyMatch(user -> email.equalsIgnoreCase(user.getEmail()));

        if (emailExist) {
            throw new EmailAlreadyExistsException("Email " + email + " is already registered");
        }
    }
}