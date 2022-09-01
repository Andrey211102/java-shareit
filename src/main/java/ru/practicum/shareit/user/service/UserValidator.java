package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserValidator {

    void validateCreate(UserDto newUser);

    void validateEmailBusy(long id, UserDto user);

    void validateGetById(long id, String action);
}
