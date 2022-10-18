package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserDtoMapper;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.exceptions.UserConflictException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    UserService service;

    @Autowired
    UserDtoMapper mapper;

    @Autowired
    UserStorage storage;

    private User user;
    private User user1;
    private User user2;

    private UserDto dto1;

    @BeforeEach
    void setUp() {
        user = storage.save(new User(null, "test", "userT@mail.test"));
        user1 = new User(null, "testUser", "user@mail.test");
        dto1 = mapper.toDto(user1);
    }

    @Test
    void shouldDoesNotThrowWhenCreate() {
        assertDoesNotThrow(() -> service.create(dto1));
    }

    @Test
    void shouldReturnEqualsDtoCreateNewUser() {

        assertAll(String.valueOf(true),
                () -> assertEquals(user1.getName(), dto1.getName()),
                () -> assertEquals(user1.getEmail(), dto1.getEmail()));
    }

    //update
    @Test
    void shouldThrowUserNotFoundExceptionWhenUpdateIncorrectId() {
        assertThrows(UserNotFoundException.class, () -> service.update(100, dto1));
    }

    @Test
    void shouldThrowUserConflictExceptionWhenUpdateDuplicateEmail() {

        user2 = storage.save(new User(null, "testUser2", "user2@mail.test"));

        UserDto savedDto = mapper.toDto(user2);
        savedDto.setEmail(user.getEmail());

        assertThrows(UserConflictException.class, () -> service.update(user2.getId(), savedDto));
    }

    @Test
    void shouldEqualsUpdatedPropertiesWhenUserUpdate() {

        User savedUser = storage.save(user1);
        user1.setName("updated");

        UserDto updated = service.update(savedUser.getId(), mapper.toDto(user1));
        assertEquals(updated.getName(), user1.getName());
    }

    //getById
    @Test
    void shouldThrowUserNotFoundExceptionWhenGetByIdIncorrectId() {
        assertThrows(UserNotFoundException.class, () -> service.getById(-1));
    }

    @Test
    void shouldReturnEqualsDtoGetById() {

        User savedUser = storage.save(user1);
        UserDto byIdDto = service.getById(savedUser.getId());

        assertAll(String.valueOf(true),
                () -> assertEquals(savedUser.getId(), byIdDto.getId()),
                () -> assertEquals(savedUser.getName(), byIdDto.getName()),
                () -> assertEquals(savedUser.getEmail(), byIdDto.getEmail()));
    }

    //deleteById
    @Test
    void shouldThrowUserNotFoundExceptionWhenDeleteIncorrectId() {
        assertThrows(UserNotFoundException.class, () -> service.deleteById(-1));
    }

    @Test
    void shouldDoesNotThrowWhenDeleteById() {
        User savedUser = storage.save(user1);
        assertDoesNotThrow(() -> service.deleteById(savedUser.getId()));
    }

    @Test
    void shouldEqualsCountUsersAfterDeleteById() {

        User savedUser1 = storage.save(new User(null, "testUser1", "user@mail.test"));
        User savedUser2 = storage.save(new User(null, "testUser2", "user@mail2.test"));
        User savedUser3 = storage.save(new User(null, "testUser3", "user@mail3.test"));

        service.deleteById(savedUser2.getId());
        assertEquals(3, service.getAll().size());
    }

    //getAll
    @Test
    void shouldDoesNotThrowWhenGetAll() {
        assertDoesNotThrow(() -> service.getAll());
    }

    @Test
    void shouldEqualsCountUsersGetAll() {

        User savedUser1 = storage.save(new User(null, "testUser1", "user@mail.test"));
        User savedUser2 = storage.save(new User(null, "testUser2", "user@mail2.test"));
        User savedUser3 = storage.save(new User(null, "testUser3", "user@mail3.test"));

        assertEquals(4, service.getAll().size());
    }
}