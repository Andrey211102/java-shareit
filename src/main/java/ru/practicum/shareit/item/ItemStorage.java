package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {

    List<Item> findByOwner_IdOrderById(Long id);

    @Query("select it " +
            "from Item as it " +
            "where it.available = true and " +
            "(lower(it.name) like lower(concat('%' ,?1 , '%')) " +
            "or lower(it.description) like lower(concat('%' , ?1 , '%')))")
    List<Item> search(String request);
}
