package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Component
public class CommentDtoMapper {

    public CommentInfDto toInfDto(Comment comment) {

        CommentInfDto dto = new CommentInfDto();

        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setCreated(comment.getCreated());

        return dto;
    }

    public Comment fromDto(CommentDto dto, Item item, User author) {

        Comment comment = new Comment();

        comment.setAuthor(author);
        comment.setItem(item);
        comment.setText(dto.getText());
        comment.setCreated(LocalDateTime.now());

        return comment;
    }
}
