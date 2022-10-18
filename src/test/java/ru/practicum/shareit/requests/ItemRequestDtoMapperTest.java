package ru.practicum.shareit.requests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestInfDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@SpringBootTest
class ItemRequestDtoMapperTest {

    @MockBean
    ItemStorage mockItemStorage;

    @Autowired
    ItemRequestDtoMapper mapper;

    static ItemRequest request;
    static User user;

    @BeforeAll
    static void beforeAll() {
        user = new User(null, "testUser1", "user1@mail.test");
        request = new ItemRequest(1, "Тестовое описание", LocalDateTime.now(), user);
    }

    @Test
    void shouldEqualsPropertysFromDto() {

        ItemRequestDto dto = new ItemRequestDto("Тестовое описание1");
        ItemRequest requestFromDto = mapper.fromDto(dto, user);

        assertAll(String.valueOf(true),
                () -> assertEquals(requestFromDto.getDescription(), dto.getDescription()),
                () -> assertEquals(requestFromDto.getUser(), user));
    }

    @Test
    void shouldequalsPropertysToInfDto() {

        Item item1 = new Item(1, "Дрель", "Описание тест", true, user, request);
        Item item2 = new Item(2, "Пила", "Описание тест", false, user, request);

        when(mockItemStorage.findAllByRequest_Id(any()))
                .thenReturn(Arrays.asList(item1, item2));

        ItemRequestInfDto infDto = mapper.toInfDto(request, mockItemStorage.findAllByRequest_Id(request.getId()));

        assertAll(String.valueOf(true),
                () -> assertEquals(request.getId(), infDto.getId()),
                () -> assertEquals(request.getCreated(), infDto.getCreated()),
                () -> assertEquals(request.getDescription(), infDto.getDescription()),
                () -> assertEquals(2, infDto.getItems().size()),
                () -> assertEquals(item1.getId(), infDto.getItems().get(0).getId()),
                () -> assertEquals(item2.getId(), infDto.getItems().get(1).getId()));
    }
}