package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto newUser);

    UserDto update(long id, UserDto user);

    UserDto getById(long id);

    void deleteById(long id);

    List<UserDto> getAll();
}
