package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfDto;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CommentDtoMapperTest {

    @Autowired
    CommentDtoMapper mapper;

    User user;
    Item item;
    static Comment comment;

    @BeforeEach
    void setUp() {
        user = new User(null, "testUser", "user@mail.test");
        ItemRequest request = new ItemRequest(1, "Тестовое описание", LocalDateTime.now(), user);
        item = new Item(0, "Дрель", "Описание тест", true, user, request);
        comment = new Comment(1L,item,user,"Комментарий",LocalDateTime.now());
    }

    @Test
    void shouldequalsPropertysToInfDto() {

        CommentInfDto dto = mapper.toInfDto(comment);

        assertAll(String.valueOf(true),
                () -> assertEquals(dto.getAuthorName(),comment.getAuthor().getName()),
                () -> assertEquals(dto.getId(),comment.getId()),
                () -> assertEquals(dto.getText(),comment.getText()));
    }

    @Test
    void shouldEqualsTextPropertysFromDto() {
        CommentDto dto = new CommentDto("Комментарий");
        assertEquals(mapper.fromDto(dto,item,user).getText(),dto.getText());
    }
}

