package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser_shouldSaveAndReturnUserDto() {
        UserDto dto = new UserDto();
        dto.setName("John");
        dto.setEmail("john.doe@example.com");

        UserDto created = userService.create(dto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("John");
        assertThat(created.getEmail()).isEqualTo("john.doe@example.com");

        Optional<User> found = userRepository.findById(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John");
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
    }



    @Test
    void createUser_withDuplicateEmail_shouldThrowException() {
        User existingUser = new User();
        existingUser.setName("John");
        existingUser.setEmail("john.doe@example.com");
        userRepository.save(existingUser);

        UserDto dto = new UserDto();
        dto.setName("New User");
        dto.setEmail("john.doe@example.com");

        assertThatThrownBy(() -> userService.create(dto))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email john.doe@example.com is already registered");
    }

    @Test
    void updateUser_shouldUpdateNameAndEmail() {
        User user = new User();
        user.setName("Old Name");
        user.setEmail("old@example.com");
        user = userRepository.save(user);

        UserDto updateDto = new UserDto();
        updateDto.setName("New Name");
        updateDto.setEmail("new@example.com");

        UserDto updated = userService.update(updateDto, user.getId());

        assertThat(updated.getId()).isEqualTo(user.getId());
        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getEmail()).isEqualTo("new@example.com");

        User persisted = userRepository.findById(user.getId()).orElseThrow();
        assertThat(persisted.getName()).isEqualTo("New Name");
        assertThat(persisted.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void updateUser_withDuplicateEmail_shouldThrowException() {
        User firstUser = new User();
        firstUser.setName("User1");
        firstUser.setEmail("user1@example.com");
        userRepository.save(firstUser);

        User secondUser = new User();
        secondUser.setName("User2");
        secondUser.setEmail("user2@example.com");
        User savedUser = userRepository.save(secondUser);

        UserDto updateDto = new UserDto();
        updateDto.setEmail("user1@example.com");

        assertThatThrownBy(() -> userService.update(updateDto, savedUser.getId()))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void getUser_shouldReturnUser() {
        User user = new User();
        user.setName("John");
        user.setEmail("john.doe@example.com");
        user = userRepository.save(user);

        UserDto found = userService.get(user.getId());

        assertThat(found.getId()).isEqualTo(user.getId());
        assertThat(found.getName()).isEqualTo("John");
        assertThat(found.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void getUser_withNonExistingId_shouldThrowNotFoundException() {
        long nonExistingId = 1L;

        assertThatThrownBy(() -> userService.get(nonExistingId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with id " + nonExistingId + " not found");
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        User firstUser = new User();
        firstUser.setName("User1");
        firstUser.setEmail("user1@example.com");
        userRepository.save(firstUser);

        User SecondUser = new User();
        SecondUser.setName("User2");
        SecondUser.setEmail("user2@example.com");
        userRepository.save(SecondUser);

        List<UserDto> all = userService.getAll();

        assertThat(all).hasSize(2);
        assertThat(all).extracting(UserDto::getEmail)
                .containsExactlyInAnyOrder("user1@example.com", "user2@example.com");
    }

    @Test
    void deleteUser_shouldRemoveUser() {
        User user = new User();
        user.setName("ToDelete");
        user.setEmail("delete@example.com");
        user = userRepository.save(user);

        userService.delete(user.getId());

        Optional<User> deleted = userRepository.findById(user.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    void deleteUser_withNonExistingId_shouldDoNothing() {
        long nonExistingId = 999L;

        userService.delete(nonExistingId);
    }
}