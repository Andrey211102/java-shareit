package ru.practicum.shareit.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestInfDto;
import ru.practicum.shareit.requests.exceptions.ItemRequestNotFoundExeption;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class ItemRequestServiceTest {

    @Autowired
    ItemRequestService service;

    @Autowired
    ItemRequestDtoMapper mapper;

    @Autowired
    ItemRequestsStorage storage;

    @Autowired
    ItemStorage itemStorage;

    @Autowired
    UserStorage userStorage;

    private ItemRequest request;
    private ItemRequestInfDto createdInfDto;
    private User user;

    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        user = userStorage.save(new User(null, "testUser", "user@mail.test"));

        request = storage.save(new ItemRequest(1L, "Тестовое описание", LocalDateTime.now(), user));

        item1 = itemStorage.save(new Item(1, "Дрель", "Описание тест", true, user, request));
        item2 = itemStorage.save(new Item(2, "Пила", "Описание тест", false, user, request));

        createdInfDto = service.create(user.getId(), new ItemRequestDto("Описание"));
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenCreateUnknowUserId() {
        assertThrows(UserNotFoundException.class, () ->
                service.create(100, new ItemRequestDto("Тестовое описание")));
    }

    @Test
    void shouldReturnEqualsDtoCreateNewItemRequest() {

        assertAll(String.valueOf(true),
                () -> assertNotNull(createdInfDto.getCreated()),
                () -> assertEquals(request.getId() + 1, createdInfDto.getId()),
                () -> assertEquals("Описание", createdInfDto.getDescription()),
                () -> assertEquals(0, createdInfDto.getItems().size()));
    }

    @Test
    void shouldThrowItemRequestNotFoundExeptionGetByIdWhenUnknowRequestId() {
        assertThrows(ItemRequestNotFoundExeption.class, () ->
                service.getById(user.getId(), 100));
    }

    @Test
    void shouldEqualsDtoGetById() {

        ItemRequestInfDto infDto = service.getById(user.getId(), request.getId());
        ItemDto itemDto = infDto.getItems().get(0);

        assertAll(String.valueOf(true),
                () -> assertEquals(request.getId(), infDto.getId()),
                () -> assertEquals(request.getDescription(), infDto.getDescription()),
                () -> assertEquals(request.getCreated(), infDto.getCreated()),

                () -> assertEquals(2, infDto.getItems().size()),
                () -> assertEquals(item1.getId(), itemDto.getId()),
                () -> assertEquals(item1.getOwner().getId(), user.getId()),
                () -> assertEquals(item1.getName(), itemDto.getName()),
                () -> assertEquals(item1.getDescription(), itemDto.getDescription()),
                () -> assertEquals(item1.getRequest().getId(), itemDto.getRequestId()));
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenGetByUseridIncorrectId() {
        assertThrows(UserNotFoundException.class, () ->
                service.getByUserid(100, 0, 10));
    }

    @Test
    void shouldEqualsGetByUserid() {

        List<ItemRequestInfDto> byUserid = service.getByUserid(user.getId(), 0, 10);
        ItemRequestInfDto infDto = byUserid.get(0);

        assertAll(String.valueOf(true),
                () -> assertEquals(2, byUserid.size()),
                () -> assertEquals(createdInfDto.getId(), infDto.getId()),
                () -> assertEquals(createdInfDto.getDescription(), infDto.getDescription()),
                () -> assertEquals(createdInfDto.getCreated(), infDto.getCreated()),
                () -> assertEquals(0, infDto.getItems().size()));
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenGetAllidIncorrectId() {
        assertThrows(UserNotFoundException.class, () ->
                service.getAll(100L, 0, 10));
    }

    @Test
    void getAll() {
        User user2 = userStorage.save(new User(null, "testUser2", "user2@mail.test"));

        ItemRequestInfDto infDto1 = service.create(user2.getId(), new ItemRequestDto("Описание"));
        ItemRequestInfDto infDto2 = service.create(user2.getId(), new ItemRequestDto("Описание2"));
        ItemRequestInfDto infDto3 = service.create(user2.getId(), new ItemRequestDto("Описание3"));

        List<ItemRequestInfDto> byUserid = service.getAll(user.getId(), 0, 10);

        assertAll(String.valueOf(true),
                () -> assertEquals(3, byUserid.size()));
    }
}