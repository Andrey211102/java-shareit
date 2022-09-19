package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentStrorage extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItem_Id(Long itemId);
}
