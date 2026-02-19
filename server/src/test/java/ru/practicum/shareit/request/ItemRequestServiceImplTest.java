package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User requester;
    private User otherUser;
    private Item fitstItem;
    private Item secondItem;

    @BeforeEach
    void setUp() {
        requester = new User();
        requester.setName("Requester");
        requester.setEmail("requester@test.com");
        requester = userRepository.save(requester);

        otherUser = new User();
        otherUser.setName("Other");
        otherUser.setEmail("other@test.com");
        otherUser = userRepository.save(otherUser);

        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(otherUser);
        fitstItem = itemRepository.save(item);

        Item itemForAnotherRequest = new Item();
        itemForAnotherRequest.setName("Hammer");
        itemForAnotherRequest.setDescription("Heavy hammer");
        itemForAnotherRequest.setAvailable(true);
        itemForAnotherRequest.setOwner(otherUser);
        secondItem = itemRepository.save(itemForAnotherRequest);
    }

    @Test
    void createRequest_shouldSaveAndReturnDto() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Need a drill");

        ItemRequestResponseDto created = itemRequestService.create(dto, requester.getId());

        assertThat(created.getId()).isNotNull();
        assertThat(created.getDescription()).isEqualTo("Need a drill");
        assertThat(created.getCreated()).isNotNull();
        assertThat(created.getItems()).isEmpty();

        ItemRequest saved = itemRequestRepository.findById(created.getId()).orElseThrow();
        assertThat(saved.getRequester().getId()).isEqualTo(requester.getId());
        assertThat(saved.getCreated()).isEqualTo(created.getCreated());
    }

    @Test
    void getRequest_withoutItems_shouldReturnDto() {
        ItemRequest request = createRequest(requester, "Need a tool");

        ItemRequestResponseDto found = itemRequestService.get(request.getId(), requester.getId());

        assertThat(found.getId()).isEqualTo(request.getId());
        assertThat(found.getDescription()).isEqualTo("Need a tool");
        assertThat(found.getCreated()).isEqualTo(request.getCreated());
        assertThat(found.getItems()).isEmpty();
    }

    @Test
    void getRequest_withItems_shouldReturnDtoWithItems() {
        ItemRequest request = createRequest(requester, "Need tools");
        fitstItem.setRequest(request);
        itemRepository.save(fitstItem);
        secondItem.setRequest(request);
        itemRepository.save(secondItem);

        ItemRequestResponseDto found = itemRequestService.get(request.getId(), requester.getId());

        assertThat(found.getItems()).hasSize(2);
        assertThat(found.getItems()).extracting("id")
                .containsExactlyInAnyOrder(fitstItem.getId(), secondItem.getId());
        assertThat(found.getItems()).extracting("name")
                .containsExactlyInAnyOrder("Drill", "Hammer");
    }

    @Test
    void getRequest_withUnknownRequestId_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> itemRequestService.get(999L, requester.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("ItemRequest with id 999 not found");
    }

    @Test
    void getItemRequestsByUser_shouldReturnUsersRequestsWithItems() {
        ItemRequest request1 = createRequest(requester, "First request");
        ItemRequest request2 = createRequest(requester, "Second request");
        request2.setCreated(LocalDateTime.now().plusSeconds(1));
        itemRequestRepository.save(request2);

        fitstItem.setRequest(request1);
        itemRepository.save(fitstItem);

        List<ItemRequestResponseDto> result = itemRequestService.getItemRequestsByUser(requester.getId());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(request2.getId());
        assertThat(result.get(1).getId()).isEqualTo(request1.getId());

        ItemRequestResponseDto dtoWithItem = result.stream()
                .filter(d -> d.getId().equals(request1.getId()))
                .findFirst().orElseThrow();
        assertThat(dtoWithItem.getItems()).hasSize(1);
        assertThat(dtoWithItem.getItems().getFirst().getId()).isEqualTo(fitstItem.getId());

        ItemRequestResponseDto dtoWithoutItem = result.stream()
                .filter(d -> d.getId().equals(request2.getId()))
                .findFirst().orElseThrow();
        assertThat(dtoWithoutItem.getItems()).isEmpty();
    }

    @Test
    void getOtherUsersRequests_shouldReturnOtherUsersRequests() {
        ItemRequest request1 = createRequest(requester, "Requester's request");
        ItemRequest request2 = createRequest(requester, "Another requester's request");
        ItemRequest request3 = createRequest(otherUser, "Other's request");
        fitstItem.setRequest(request1);
        itemRepository.save(fitstItem);

        List<ItemRequestResponseDto> result = itemRequestService.getOtherUsersRequests(requester.getId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(request3.getId());
        assertThat(result.getFirst().getDescription()).isEqualTo("Other's request");
        assertThat(result.getFirst().getItems()).isEmpty(); // у request3 нет предметов
    }

    private ItemRequest createRequest(User user, String description) {
        ItemRequest request = new ItemRequest();
        request.setDescription(description);
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(request);
    }
}