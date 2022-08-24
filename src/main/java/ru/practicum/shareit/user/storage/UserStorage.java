package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User create(User newUser);

    User update(long id, UserDto user);

    User getById(long id);

    void delete(long id);

    boolean emailIsBusy(String email);

    User userByMail(String email);

    List<User> getAll();
}
