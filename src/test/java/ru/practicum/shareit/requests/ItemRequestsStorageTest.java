package ru.practicum.shareit.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestsStorageTest {

    User user1;
    User user2;

    ItemRequest request1;
    ItemRequest request2;
    ItemRequest request3;

    @Autowired
    ItemRequestsStorage storage;

    @Autowired
    UserStorage userStorage;

    @BeforeEach
    void setUp() {
        user1 = userStorage.save(new User(null, "testUser1", "user1@mail.test"));
        user2 = userStorage.save(new User(null, "testUser2", "user2@mail.test"));

        request1 = storage.save(new ItemRequest(1, "Тестовое описание", LocalDateTime.now(), user1));
        request2 = storage.save(new ItemRequest(2, "Тестовое описание", LocalDateTime.now(), user1));
        request3 = storage.save(new ItemRequest(3, "Тестовое описание", LocalDateTime.now(), user2));
    }

    //findByUser
    @Test
    void shouldEqualsCountUsersFindByUser() {
        assertEquals(2, storage.findByUser(user1.getId(), PageRequest.ofSize(10)).size());
    }

    @Test
    void shouldSortByCreatedUsersFindByUser() {

        List<ItemRequest> requests = storage.findByUser(user1.getId(), PageRequest.ofSize(10));
        assertTrue(requests.get(0).getCreated().isAfter(requests.get(1).getCreated()));
    }

    @Test
    void findAllOtherUsers() {
        assertEquals(1, storage.findAllOtherUsers(user1.getId(), PageRequest.ofSize(10))
                .getTotalElements());
    }

    @Test
    void shouldEqualsCountUsersFindAllOtherUsers() {

        Page<ItemRequest> requests = storage.findAllOtherUsers(user1.getId(), PageRequest.ofSize(10));

        ItemRequest request = storage.findAllOtherUsers(user1.getId(), PageRequest.ofSize(10))
                .getContent()
                .get(0);

        assertAll(String.valueOf(true),
                () -> assertTrue(request3.getId() == request.getId()),
                () -> assertTrue(request3.getUser() == request.getUser()),
                () -> assertTrue(request3.getCreated() == request.getCreated()),
                () -> assertTrue(request3.getDescription().equals(request.getDescription())));
    }
}