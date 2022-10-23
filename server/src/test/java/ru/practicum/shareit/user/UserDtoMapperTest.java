package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserDtoMapperTest {

    @Autowired
    UserDtoMapper mapper;

    static User user;

    @BeforeAll
    static void beforeAll() {
        user = new User(1L, "testUser", "user@mail.test");
    }

    @Test
    void shouldEqualsPropertysFromDto() {

        User userFromDto = mapper.fromDto(mapper.toDto(user));

        assertAll(String.valueOf(true),
                () -> assertEquals(user.getEmail(), userFromDto.getEmail()),
                () -> assertEquals(user.getName(), userFromDto.getName()));
    }

    @Test
    void shouldequalsPropertysToDto() {

        UserDto dto = mapper.toDto(user);

        assertAll(String.valueOf(true),
                () -> assertEquals(user.getId(), dto.getId()),
                () -> assertEquals(user.getEmail(), dto.getEmail()),
                () -> assertEquals(user.getName(), dto.getName()));
    }
}