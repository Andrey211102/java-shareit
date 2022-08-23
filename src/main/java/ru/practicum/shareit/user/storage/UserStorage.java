package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    UserDto create(UserDto newUser);

    UserDto update(long id, UserDto user);

    UserDto getByIdDto(long id);

    User getById(long id);

    void delete(long id);

    List<UserDto> getAll();
}
