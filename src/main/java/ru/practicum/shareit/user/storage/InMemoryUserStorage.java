package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {
    private long counter = 1L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        user.setId(generatedId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> get(long id) {
        return Optional.ofNullable(users.get(id));
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
}