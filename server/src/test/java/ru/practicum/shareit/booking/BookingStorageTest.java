package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingStorageTest {

    @Autowired
    BookingStorage storage;

    @Autowired
    UserStorage userStorage;

    @Autowired
    ItemStorage itemStorage;

    private Item item;
    private Item item2;
    private Booking booking;
    private Booking booking2;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = userStorage.save(new User(null, "testUser1", "user1@mail.test"));
        user2 = userStorage.save(new User(null, "testUser2", "user2@mail.test"));

        item = itemStorage.save(new Item(1L, "Дрель", "Описание тест", true, user1, null));
        item2 = itemStorage.save(new Item(2L, "Кусачки", "Описание тест", false, user1, null));

        booking = storage.save(new Booking(1L, item, BookingStatus.WAITING, user1, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(10)));

        booking2 = storage.save(new Booking(2L, item2, BookingStatus.WAITING, user2, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(10)));
    }

    @Test
    void shouldEqualsIdFindByIdAndBookerOrOwner() {

        Booking finded1 = storage.findByIdAndBookerOrOwner(booking.getId(), booking.getBooker().getId());
        Booking finded2 = storage.findByIdAndBookerOrOwner(booking.getId(), booking.getItem().getOwner().getId());

        assertEquals(finded1.getId(), finded2.getId());
    }

    @Test
    void shouldEqualsCountfindByBookerAndDatesFuture() {

        Page page = storage.findByBookerAndDatesFuture(user1.getId(),
                LocalDateTime.now(), PageRequest.of(0, 10));

        assertEquals(1, page.getContent().size());
    }
}