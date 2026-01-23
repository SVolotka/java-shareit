package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    @Override
    public UserDto create(UserDto userDto) {
        if ((userDto.getName() == null) || (userDto.getEmail() == null)) {
            throw new IllegalArgumentException();
        }

        User newUser = UserMapper.dtoToUser(userDto);
        inMemoryUserStorage.create(newUser);

        return UserMapper.userToDto(newUser);
    }

    @Override
    public UserDto update(UserDto userDto, long id) {
        User user = UserMapper.dtoToUser(userDto);
        user.setId(id);
        User exsitingUser = inMemoryUserStorage.update(user);
        return UserMapper.userToDto(exsitingUser);
    }

    @Override
    public UserDto get(long id) {
        User existingUser = inMemoryUserStorage.get(id);
        return UserMapper.userToDto(existingUser);
    }

    @Override
    public List<UserDto> getAll() {
        return inMemoryUserStorage.getAll()
                .stream()
                .map(UserMapper::userToDto)
                .toList();
    }

    @Override
    public void delete(long id) {
        inMemoryUserStorage.delete(id);
    }
}