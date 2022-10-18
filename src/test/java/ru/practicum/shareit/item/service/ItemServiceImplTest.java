package ru.practicum.shareit.item.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDtoMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfDto;
import ru.practicum.shareit.item.exceptions.ItemForbiddenException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.exceptions.ItemValidationException;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestsStorage;
import ru.practicum.shareit.requests.exceptions.ItemRequestNotFoundExeption;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class ItemServiceImplTest {

    @Autowired
    ItemService service;
    @Autowired
    ItemDtoMapper mapper;
    @Autowired
    ItemStorage storage;
    @Autowired
    ItemRequestsStorage requestsStorage;
    @Autowired
    UserStorage userStorage;
    @Autowired
    BookingStorage bookingStorage;

    private User user;
    private User user2;
    private Item item;
    private Item item2;
    private ItemRequest request;
    private ItemDto dto;

    @BeforeEach
    void setUp() {
        user = userStorage.save(new User(null, "testUser", "user@mail.test"));
        user2 = userStorage.save(new User(null, "testUser2", "user2@mail.test"));

        request = requestsStorage.save(new ItemRequest(1L, "Тестовое описание", LocalDateTime.now(), user));
        item = storage.save(new Item(1L, "Дрель", "Описание тест", true, user, request));
        item2 = storage.save(new Item(2L, "Кусачки", "Описание тест", true, user, null));
        dto = mapper.toDto(item);
    }

    //create
    @Test
    void shouldThrowItemValidationExceptionCreateIncorrectIdOwner() {
        assertThrows(ItemValidationException.class, () -> service.create(-2, dto));
    }

    @Test
    void shouldThrowUserNotFoundExceptionCreateUnknowIdOwner() {
        assertThrows(UserNotFoundException.class, () -> service.create(500L, dto));
    }

    @Test
    void shouldThrowItemRequestNotFoundExeptionCreateUnknowIdRequest() {
        dto.setRequestId(44L);
        assertThrows(ItemRequestNotFoundExeption.class, () -> service.create(user.getId(), dto));
    }

    @Test
    void shouldDoesNotThrowCreate() {
        assertDoesNotThrow(() -> service.create(user.getId(), dto));
    }

    //getById
    @Test
    void shouldDoesNotThrowGetById() {
        assertDoesNotThrow(() -> service.getById(item.getId(), user.getId()));
    }

    @Test
    void shouldEqulsIdGetById() {
        assertEquals(item.getId(), service.getById(item.getId(), item.getOwner().getId()).getId());
    }

    //update
    @Test
    void shouldThrowItemNotFoundExceptionUnknowItemIdUpdate() {
        assertThrows(ItemNotFoundException.class, () -> service.update(user.getId(), 5, dto));
    }

    @Test
    void shouldThrowItemValidationExceptionIncorrectIdOwnerUpdate() {
        assertThrows(ItemValidationException.class, () -> service.update(-2, item.getId(), dto));
    }

    @Test
    void shouldDoesNotThrowUpdate() {
        dto.setAvailable(false);
        assertDoesNotThrow(() -> service.update(user.getId(), item.getId(), dto));
    }

    @Test
    void shouldThrowItemForbiddenExceptionUnknowOwnerUpdate() {
        dto.setAvailable(false);
        assertThrows(ItemForbiddenException.class, () -> service.update(user2.getId(), item.getId(), dto));
    }

    @Test
    void shouldEqualsPropertysAfterUpdate() {
        dto.setAvailable(false);
        dto.setName("newName");
        dto.setDescription("newDesc");

        service.update(user.getId(), item.getId(), dto);

        ItemInfDto updated = service.getById(item.getId(), item.getOwner().getId());

        assertAll(String.valueOf(true),
                () -> assertEquals(item.isAvailable(), updated.getAvailable()),
                () -> assertEquals(item.getDescription(), updated.getDescription()),
                () -> assertEquals(item.getName(), updated.getName()));
    }

    //getByOwner
    @Test
    void shouldEqulsCountGetByOwner() {
        List<ItemInfDto> infDtos = service.getByOwner(user.getId(), 0, 10);
        assertEquals(2, infDtos.size());
    }

    //addComment
    @Test
    void shouldThrowItemValidationExceptionAddCommentWrongTime() {

        BookingDtoMapper bookingDtoMapper = new BookingDtoMapper();
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusHours(-12),
                LocalDateTime.now().plusHours(10));
        Booking booking = bookingStorage.save(bookingDtoMapper.fromDto(bookingDto, item, user, BookingStatus.APPROVED));

        assertThrows(ItemValidationException.class,
                () -> service.addComment(user.getId(), item.getId(), new CommentDto("Комментарий")));
    }

    @Test
    void shouldDoesNotThrowAddComment() {

        BookingDtoMapper bookingDtoMapper = new BookingDtoMapper();
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusHours(-12),
                LocalDateTime.now().plusHours(-10));
        Booking booking = bookingStorage.save(bookingDtoMapper.fromDto(bookingDto, item, user2, BookingStatus.APPROVED));

        assertDoesNotThrow(() -> service.addComment(user2.getId(), item.getId(), new CommentDto("Комментарий")));
    }

    //search
    @Test
    void shouldEqulsCountAndIdSearch() {

        List<ItemDto> finded = service.search("ДреЛь", 0, 10);
        assertAll(String.valueOf(true),
                () -> assertEquals(1, finded.size()),
                () -> assertEquals(item.getId(), finded.get(0).getId()));
    }

    @Test
    void shouldEqualsQComment() {
        Comment comment = new Comment(1L, item, user, "Комментарий", LocalDateTime.now());
        BooleanExpression byId = QComment.comment.id.eq(comment.getId());
        assertNotEquals(byId, comment.getId());
    }
}