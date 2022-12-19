package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfDto;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ItemDtoMapperTest {

    @Autowired
    ItemDtoMapper mapper;

    static User user;
    static Item item;
    static ItemRequest request;
    static ItemDto dto;

    @BeforeAll
    static void beforeAll() {
        user = new User(null, "testUser", "user@mail.test");
        request = new ItemRequest(1, "Тестовое описание", LocalDateTime.now(), user);
        item = new Item(0, "Дрель", "Описание тест", true, user, request);
    }

    @Test
    void shouldequalsPropertysToDto() {

        ItemDto dto = mapper.toDto(item);

        assertAll(String.valueOf(true),
                () -> assertEquals(item.getId(), dto.getId()),
                () -> assertEquals(item.getName(), dto.getName()),
                () -> assertEquals(item.getDescription(), dto.getDescription()),
                () -> assertEquals(item.isAvailable(), dto.getAvailable()),
                () -> assertEquals(item.getRequest().getId(), dto.getRequestId()));
    }

    @Test
    void shouldEqualsPropertysFromDto() {

        Item fromDto = mapper.fromDto(mapper.toDto(item), user, Optional.of(request));

        assertAll(String.valueOf(true),
                () -> assertEquals(item.getId(), fromDto.getId()),
                () -> assertEquals(item.getName(), fromDto.getName()),
                () -> assertEquals(item.getDescription(), fromDto.getDescription()),
                () -> assertEquals(item.isAvailable(), fromDto.isAvailable()),
                () -> assertEquals(item.getRequest(), fromDto.getRequest()),
                () -> assertEquals(item.getOwner(), user));
    }

    @Test
    void shouldEqualsPropertysToInfDto() {

        Booking nextBooking = new Booking(1L, item, BookingStatus.WAITING, user, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(10));

        Booking lastBooking = new Booking(2L, item, BookingStatus.WAITING, user, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(10));

        ItemInfDto infDto = mapper.toInfDto(item, Optional.of(lastBooking),
                Optional.of(nextBooking), new ArrayList<>());

        assertAll(String.valueOf(true),
                () -> assertEquals(item.getId(), infDto.getId()),
                () -> assertEquals(item.getName(), infDto.getName()),
                () -> assertEquals(item.getDescription(), infDto.getDescription()),
                () -> assertEquals(item.isAvailable(), infDto.getAvailable()),
                () -> assertEquals(lastBooking.getId(), infDto.getLastBooking().getId()),
                () -> assertEquals(nextBooking.getId(), infDto.getNextBooking().getId()),
                () -> assertEquals(0, infDto.getComments().size()));
    }
}

