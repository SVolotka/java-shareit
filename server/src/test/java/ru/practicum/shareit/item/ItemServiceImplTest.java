package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@test.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@test.com");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);
    }

    @Test
    void createItem_shouldSaveAndReturnDto() {
        ItemDto dto = new ItemDto();
        dto.setName("Hammer");
        dto.setDescription("Heavy hammer");
        dto.setAvailable(true);

        ItemResponseDto created = itemService.create(dto, owner.getId());

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Hammer");
        assertThat(created.getDescription()).isEqualTo("Heavy hammer");
        assertThat(created.getAvailable()).isTrue();

        Item found = itemRepository.findById(created.getId()).orElseThrow();
        assertThat(found.getOwner().getId()).isEqualTo(owner.getId());
    }

    @Test
    void createItem_withUnknownUser_shouldThrowNotFoundException() {
        ItemDto dto = new ItemDto();
        dto.setName("Hammer");
        dto.setDescription("Heavy");
        dto.setAvailable(true);

        assertThatThrownBy(() -> itemService.create(dto, 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with id 999 not found");
    }

    @Test
    void updateItem_shouldUpdateFields() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Drill");
        updateDto.setDescription("Even more powerful");
        updateDto.setAvailable(false);

        ItemResponseDto updated = itemService.update(updateDto, item.getId(), owner.getId());

        assertThat(updated.getName()).isEqualTo("Updated Drill");
        assertThat(updated.getDescription()).isEqualTo("Even more powerful");
        assertThat(updated.getAvailable()).isFalse();

        Item persisted = itemRepository.findById(item.getId()).orElseThrow();
        assertThat(persisted.getName()).isEqualTo("Updated Drill");
        assertThat(persisted.getDescription()).isEqualTo("Even more powerful");
        assertThat(persisted.getAvailable()).isFalse();
    }

    @Test
    void updateItem_withPartialFields_shouldUpdateOnlyProvided() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Only name changed");

        ItemResponseDto updated = itemService.update(updateDto, item.getId(), owner.getId());

        assertThat(updated.getName()).isEqualTo("Only name changed");
        assertThat(updated.getDescription()).isEqualTo("Powerful drill"); // unchanged
        assertThat(updated.getAvailable()).isTrue(); // unchanged
    }

    @Test
    void updateItem_byNonOwner_shouldThrowNotOwnerException() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Hacker");

        assertThatThrownBy(() -> itemService.update(updateDto, item.getId(), booker.getId()))
                .isInstanceOf(NotOwnerException.class)
                .hasMessageContaining("Only item owner can modify it");
    }

    @Test
    void getItem_byOwner_shouldReturnWithBookings() {
        Booking pastBooking = new Booking();
        pastBooking.setStart(LocalDateTime.now().minusDays(10));
        pastBooking.setEnd(LocalDateTime.now().minusDays(5));
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);

        Booking futureBooking = new Booking();
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(3));
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(futureBooking);

        Comment comment = new Comment();
        comment.setText("Great!");
        comment.setItem(item);
        comment.setAuthor(booker);
        comment.setCreatedDate(LocalDateTime.now().minusDays(1));
        commentRepository.save(comment);

        ItemResponseDto dto = itemService.get(item.getId(), owner.getId());

        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getComments()).hasSize(1);
        assertThat(dto.getComments().getFirst().getText()).isEqualTo("Great!");

        assertThat(dto.getLastBooking()).isNotNull();
        assertThat(dto.getLastBooking().getId()).isEqualTo(pastBooking.getId());

        assertThat(dto.getNextBooking()).isNotNull();
        assertThat(dto.getNextBooking().getId()).isEqualTo(futureBooking.getId());
    }

    @Test
    void getItem_unknownItem_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> itemService.get(999L, owner.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Item with id 999 not found");
    }

    @Test
    void getItemsByUser_shouldReturnOnlyUsersItems() {
        User other = new User();
        other.setName("Other");
        other.setEmail("other@test.com");
        other = userRepository.save(other);

        Item otherItem = new Item();
        otherItem.setName("Other's item");
        otherItem.setDescription("Desc");
        otherItem.setAvailable(true);
        otherItem.setOwner(other);
        itemRepository.save(otherItem);

        List<ItemResponseDto> items = itemService.getItemsByUser(owner.getId());

        assertThat(items).hasSize(1);
        assertThat(items.getFirst().getId()).isEqualTo(item.getId());
    }

    @Test
    void deleteItem_byOwner_shouldRemove() {
        itemService.delete(item.getId(), owner.getId());

        assertThat(itemRepository.findById(item.getId())).isEmpty();
    }

    @Test
    void deleteItem_byNonOwner_shouldThrowNotOwnerException() {
        assertThatThrownBy(() -> itemService.delete(item.getId(), booker.getId()))
                .isInstanceOf(NotOwnerException.class)
                .hasMessageContaining("Only item owner can delete it");
    }

    @Test
    void searchItems_shouldFindByNameOrDescription() {
        Item hammer = new Item();
        hammer.setName("Hammer");
        hammer.setDescription("Heavy hammer");
        hammer.setAvailable(true);
        hammer.setOwner(owner);
        itemRepository.save(hammer);

        Item drill = new Item();
        drill.setName("Drill");
        drill.setDescription("Electric drill");
        drill.setAvailable(true);
        drill.setOwner(owner);
        itemRepository.save(drill);

        List<ItemResponseDto> found = itemService.searchItems("hammer");

        assertThat(found).hasSize(1);
        assertThat(found.getFirst().getName()).isEqualTo("Hammer");

        found = itemService.searchItems("electric");
        assertThat(found).hasSize(1);
        assertThat(found.getFirst().getName()).isEqualTo("Drill");

        found = itemService.searchItems("nonexistent");
        assertThat(found).isEmpty();
    }

    @Test
    void createComment_success() {
        Booking finishedBooking = new Booking();
        finishedBooking.setStart(LocalDateTime.now().minusDays(10));
        finishedBooking.setEnd(LocalDateTime.now().minusDays(1));
        finishedBooking.setItem(item);
        finishedBooking.setBooker(booker);
        finishedBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(finishedBooking);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Excellent tool!");

        CommentDto created = itemService.createComment(commentDto, item.getId(), booker.getId());

        assertThat(created.getId()).isNotNull();
        assertThat(created.getText()).isEqualTo("Excellent tool!");
        assertThat(created.getAuthorName()).isEqualTo(booker.getName());
        assertThat(created.getCreated()).isNotNull();

        Comment saved = commentRepository.findById(created.getId()).orElseThrow();
        assertThat(saved.getItem().getId()).isEqualTo(item.getId());
        assertThat(saved.getAuthor().getId()).isEqualTo(booker.getId());
    }

    @Test
    void createComment_withoutCompletedBooking_shouldThrowValidationException() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Bad");

        assertThatThrownBy(() -> itemService.createComment(commentDto, item.getId(), booker.getId()))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Only users who have rented and completed this item can leave a review");
    }
}