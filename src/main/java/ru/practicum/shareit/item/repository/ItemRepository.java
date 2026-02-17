package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId);

    @Query("""
    select i from Item as i
    where (upper(i.name) like upper(concat('%', ?1, '%'))
    or upper(i.description) like upper(concat('%', ?1, '%')))
    and i.available = true
    """)
    List<Item> search(String text);

   // List<Item> findAllByRequestId(Long requestId);

    @Query("SELECT i FROM Item i WHERE i.request.id = :requestId")
    List<Item> findAllByRequestId(@Param("requestId") Long requestId);
}
