package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User newUser = UserMapper.dtoToUser(userDto);
        validateEmail(newUser.getEmail());
        User createdUser = userRepository.save(newUser);

        return UserMapper.userToDto(createdUser);
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, long id) {
        User user = UserMapper.dtoToUser(userDto);
        user.setId(id);
        System.out.println(userDto);
        System.out.println(id);
        System.out.println(user);

        System.out.println("до проверки");

        User existingUser = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("User with id " + id + " not found"));

        System.out.println("после проверки");
        System.out.println(existingUser);
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }

        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
                throw new EmailAlreadyExistsException("...");
            }
            existingUser.setEmail(user.getEmail());
        }

        User updatedUser = userRepository.save(existingUser);
        return UserMapper.userToDto(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto get(long id) {
        User existingUser = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("User with id " + id + " not found"));
        return UserMapper.userToDto(existingUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::userToDto)
                .toList();
    }

    @Override
    @Transactional
    public void delete(long id) {
        userRepository.deleteById(id);
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyExistsException("Email " + email + " is already registered");
        }
    }
}