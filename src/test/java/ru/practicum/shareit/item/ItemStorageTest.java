package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestsStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemStorageTest {

    @Autowired
    ItemStorage storage;

    @Autowired
    UserStorage userStorage;

    @Autowired
    ItemRequestsStorage requestsStorage;

    Item item1;
    Item item2;
    User user;
    ItemRequest request;

    @BeforeEach
    void setUp() {
        user = userStorage.save(new User(null, "testUser", "user@mail.test"));
        request = requestsStorage.save(new ItemRequest(1, "Тестовое описание", LocalDateTime.now(), user));

        item1 = storage.save(new Item(0, "Дрель", "Описание тест", true, user, null));
        item2 = storage.save(new Item(0, "Отвертка", "Отвертка не дрель", true, user, request));
    }

    @Test
    void shouldEquallsCountFindedItemsSearch() {

        Page<Item> finded = storage.search("ДреЛь", PageRequest.of(0, 10));
        Page<Item> findedInvalid = storage.search("Java", PageRequest.of(0, 10));

        assertAll(String.valueOf(true),
                () -> assertEquals(2, finded.getTotalElements()),
                () -> assertEquals(0, findedInvalid.getTotalElements()));
    }

    @Test
    void shouldEquallsFindedItem() {

        Page<Item> finded = storage.search("Отвертка", PageRequest.of(0, 10));
        Item findedItem = finded.getContent().get(0);

        assertAll(String.valueOf(true),
                () -> assertEquals(findedItem.getId(), item2.getId()),
                () -> assertEquals(findedItem.getName(), item2.getName()),
                () -> assertEquals(findedItem.getDescription(), item2.getDescription()),
                () -> assertEquals(findedItem.isAvailable(), item2.isAvailable()),
                () -> assertEquals(findedItem.getOwner(), item2.getOwner()),
                () -> assertEquals(findedItem.getRequest(), item2.getRequest()));
    }

    @Test
    void shouldEquallsCountFindAllByRequest_Id() {

        List<Item> finded = storage.findAllByRequest_Id(request.getId());
        List<Item> invalidFinded = storage.findAllByRequest_Id(10L);

        assertAll(String.valueOf(true),
                () -> assertEquals(1, finded.size()),
                () -> assertEquals(0, invalidFinded.size()));
    }

    @Test
    void shouldEqualsCountAndSortFindByOwner_IdOrderById() {
        Page<Item> finded = storage.findByOwner_IdOrderById(user.getId(), PageRequest.of(0, 10));

        assertAll(String.valueOf(true),
                () -> assertEquals(2, finded.getContent().size()),
                () -> assertEquals(item1, finded.getContent().get(0)),
                () -> assertEquals(item2, finded.getContent().get(1)));
    }
}