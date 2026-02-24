package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

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
    void createBooking_shouldSaveAndReturnDto() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(item.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponseDto created = bookingService.create(requestDto, booker.getId());

        assertThat(created.getId()).isNotNull();
        assertThat(created.getStart()).isEqualTo(requestDto.getStart());
        assertThat(created.getEnd()).isEqualTo(requestDto.getEnd());
        assertThat(created.getItem().getId()).isEqualTo(item.getId());
        assertThat(created.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(created.getStatus()).isEqualTo(BookingStatus.WAITING);

        Booking saved = bookingRepository.findById(created.getId()).orElseThrow();
        assertThat(saved.getItem().getId()).isEqualTo(item.getId());
        assertThat(saved.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void createBooking_withInvalidDates_shouldThrowValidationException() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(item.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(2));
        requestDto.setEnd(LocalDateTime.now().plusDays(1));

        assertThatThrownBy(() -> bookingService.create(requestDto, booker.getId()))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Start date must be before end date");
    }

    @Test
    void createBooking_byOwner_shouldThrowValidationException() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(item.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        assertThatThrownBy(() -> bookingService.create(requestDto, owner.getId()))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Owner cannot book his own item");
    }

    @Test
    void createBooking_withUnavailableItem_shouldThrowValidationException() {
        item.setAvailable(false);
        itemRepository.save(item);

        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(item.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        assertThatThrownBy(() -> bookingService.create(requestDto, booker.getId()))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Item it is not available for booking");
    }

    @Test
    void approveBooking_shouldSetStatusApproved() {
        Booking booking = createWaitingBooking();

        BookingResponseDto approved = bookingService.approveOrRejectBooking(booking.getId(), owner.getId(), true);

        assertThat(approved.getStatus()).isEqualTo(BookingStatus.APPROVED);
        Booking updated = bookingRepository.findById(booking.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approveBooking_byNonOwner_shouldThrowValidationException() {
        Booking booking = createWaitingBooking();

        assertThatThrownBy(() -> bookingService.approveOrRejectBooking(booking.getId(), booker.getId(), true))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Only the owner of this item can approve the booking");
    }

    @Test
    void getBooking_shouldReturnDto() {
        Booking booking = createWaitingBooking();

        BookingResponseDto found = bookingService.get(booking.getId(), booker.getId());

        assertThat(found.getId()).isEqualTo(booking.getId());
        assertThat(found.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(found.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void getBooking_byOtherUser_shouldThrowValidationException() {
        User other = new User();
        other.setName("Other");
        other.setEmail("other@test.com");
        User savedOther = userRepository.save(other);

        Booking booking = createWaitingBooking();

        assertThatThrownBy(() -> bookingService.get(booking.getId(), savedOther.getId()))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Only the owner of this item or the person who made the booking can view this booking");
    }

    @Test
    void getAllBookingsByBooker_withDifferentStates_shouldReturnCorrectLists() {
        Booking past = createBooking(booker, item, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(3), BookingStatus.APPROVED);
        Booking current = createBooking(booker, item, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), BookingStatus.APPROVED);
        Booking future = createBooking(booker, item, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4), BookingStatus.APPROVED);
        Booking waiting = createBooking(booker, item, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(6), BookingStatus.WAITING);
        Booking rejected = createBooking(booker, item, LocalDateTime.now().plusDays(7), LocalDateTime.now().plusDays(8), BookingStatus.REJECTED);

        List<BookingResponseDto> all = bookingService.getAllBookingsByBooker(booker.getId(), BookingState.ALL);
        assertThat(all).hasSize(5);
        assertThat(all).extracting(BookingResponseDto::getId)
                .containsExactlyInAnyOrder(past.getId(), current.getId(), future.getId(), waiting.getId(), rejected.getId());

        List<BookingResponseDto> pastList = bookingService.getAllBookingsByBooker(booker.getId(), BookingState.PAST);
        assertThat(pastList).hasSize(1);
        assertThat(pastList.getFirst().getId()).isEqualTo(past.getId());

        List<BookingResponseDto> currentList = bookingService.getAllBookingsByBooker(booker.getId(), BookingState.CURRENT);
        assertThat(currentList).hasSize(1);
        assertThat(currentList.getFirst().getId()).isEqualTo(current.getId());

        List<BookingResponseDto> futureList = bookingService.getAllBookingsByBooker(booker.getId(), BookingState.FUTURE);
        assertThat(futureList).hasSize(3);
        assertThat(futureList).extracting(BookingResponseDto::getId)
                .containsExactlyInAnyOrder(future.getId(), waiting.getId(), rejected.getId());

        List<BookingResponseDto> waitingList = bookingService.getAllBookingsByBooker(booker.getId(), BookingState.WAITING);
        assertThat(waitingList).hasSize(1);
        assertThat(waitingList.getFirst().getId()).isEqualTo(waiting.getId());

        List<BookingResponseDto> rejectedList = bookingService.getAllBookingsByBooker(booker.getId(), BookingState.REJECTED);
        assertThat(rejectedList).hasSize(1);
        assertThat(rejectedList.getFirst().getId()).isEqualTo(rejected.getId());
    }

    @Test
    void getAllBookingsByOwner_withDifferentStates_shouldReturnCorrectLists() {
        User otherBooker = new User();
        otherBooker.setName("OtherBooker");
        otherBooker.setEmail("other@booker.com");
        otherBooker = userRepository.save(otherBooker);

        Booking past = createBooking(booker, item, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(3), BookingStatus.APPROVED);
        Booking current = createBooking(otherBooker, item, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), BookingStatus.APPROVED);
        Booking future = createBooking(booker, item, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4), BookingStatus.APPROVED);
        Booking waiting = createBooking(otherBooker, item, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(6), BookingStatus.WAITING);
        Booking rejected = createBooking(booker, item, LocalDateTime.now().plusDays(7), LocalDateTime.now().plusDays(8), BookingStatus.REJECTED);

        List<BookingResponseDto> all = bookingService.getAllBookingsByOwner(owner.getId(), BookingState.ALL);
        assertThat(all).hasSize(5);

        List<BookingResponseDto> pastList = bookingService.getAllBookingsByOwner(owner.getId(), BookingState.PAST);
        assertThat(pastList).hasSize(1);
        assertThat(pastList.getFirst().getId()).isEqualTo(past.getId());

        List<BookingResponseDto> currentList = bookingService.getAllBookingsByOwner(owner.getId(), BookingState.CURRENT);
        assertThat(currentList).hasSize(1);
        assertThat(currentList.getFirst().getId()).isEqualTo(current.getId());

        List<BookingResponseDto> futureList = bookingService.getAllBookingsByOwner(owner.getId(), BookingState.FUTURE);
        assertThat(futureList).hasSize(3);
        assertThat(futureList).extracting(BookingResponseDto::getId)
                .containsExactlyInAnyOrder(future.getId(), waiting.getId(), rejected.getId());

        List<BookingResponseDto> waitingList = bookingService.getAllBookingsByOwner(owner.getId(), BookingState.WAITING);
        assertThat(waitingList).hasSize(1);
        assertThat(waitingList.getFirst().getId()).isEqualTo(waiting.getId());

        List<BookingResponseDto> rejectedList = bookingService.getAllBookingsByOwner(owner.getId(), BookingState.REJECTED);
        assertThat(rejectedList).hasSize(1);
        assertThat(rejectedList.getFirst().getId()).isEqualTo(rejected.getId());
    }

    private Booking createWaitingBooking() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    private Booking createBooking(User booker, Item item, LocalDateTime start, LocalDateTime end, BookingStatus status) {
        Booking booking = new Booking();
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }
}