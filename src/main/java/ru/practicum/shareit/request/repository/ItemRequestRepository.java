package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

    public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
        // Все запросы конкретного пользователя, отсортированные по дате создания убыванию
        List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Long requesterId);

        // Запросы других пользователей (все, кроме указанного), сортировка по убыванию даты
        List<ItemRequest> findAllByRequesterIdNotOrderByCreatedDesc(Long requesterId);
    }
