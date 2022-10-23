package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserStorageTest {

    @Autowired
    UserStorage storage;

    @Test
    void shouldEquallsFindByEmailEqualsIgnoreCase() {

        User userNew = storage.save(new User(null, "testUser", "user@mail.test"));

        User findedUser = storage.findByEmailEqualsIgnoreCase("USer@Mail.test").orElseThrow();

        assertAll(String.valueOf(true),
                () -> assertTrue(findedUser.getEmail().equals(userNew.getEmail())),
                () -> assertTrue(findedUser.getId() == userNew.getId()),
                () -> assertTrue(findedUser.getName().equals(userNew.getName())));
    }
}