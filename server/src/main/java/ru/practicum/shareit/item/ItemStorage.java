package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {

    Page<Item> findByOwner_IdOrderById(Long id,PageRequest pageRequest);

    List<Item> findAllByRequest_Id(Long id);

    @Query("select it " +
            "from Item as it " +
            "where it.available = true and " +
            "(lower(it.name) like lower(concat('%' ,?1 , '%')) " +
            "or lower(it.description) like lower(concat('%' , ?1 , '%')))")
    Page<Item> search(String request, PageRequest pageRequest);
}
