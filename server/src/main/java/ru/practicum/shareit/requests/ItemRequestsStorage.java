package ru.practicum.shareit.requests;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface ItemRequestsStorage extends JpaRepository<ItemRequest, Long>, QuerydslPredicateExecutor<ItemRequest> {

    @Query("select ir from ItemRequest as ir " +
            "where ir.user.id = ?1 " +
            "order by ir.created desc")
    List<ItemRequest> findByUser(Long userId, PageRequest request);

    @Query("select ir from ItemRequest as ir " +
            "where ir.user.id <> ?1 " +
            "order by ir.created desc")
    Page<ItemRequest> findAllOtherUsers(Long userId, PageRequest request);
}
