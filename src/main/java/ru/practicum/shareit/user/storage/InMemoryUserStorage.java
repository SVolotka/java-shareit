package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private long counter = 1L;

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        validateEmail(user.getEmail());
        user.setId(generatedId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        User existingUser = get(user.getId());

        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            validateEmail(user.getEmail());
            existingUser.setEmail(user.getEmail());
        }

        return existingUser;
    }

    @Override
    public User get(long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        throw new UserNotFoundException("User with id " + id + " not found");
    }

    @Override
    public List<User> getAll() {
        return List.copyOf(users.values());
    }

    @Override
    public void delete(long userId) {
        get(userId);
        users.remove(userId);
    }

    private long generatedId() {
        return counter++;
    }

    private void validateEmail(String email) {
        boolean emailExist = users.values().stream()
                .anyMatch(user -> email.equalsIgnoreCase(user.getEmail()));

        if (emailExist) {
            throw new EmailAlreadyExistsException("Email " + email + " is already registered");
        }
    }
}
