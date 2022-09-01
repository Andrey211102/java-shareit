package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserConflictException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.service.UserValidator;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
@RequiredArgsConstructor
public class UserValidatorImpl implements UserValidator {

    private final UserStorage storage;

    @Override
    public void validateCreate(UserDto newUser) {

        if (storage.emailIsBusy(newUser.getEmail())) {
            throw new UserConflictException("Cоздание пользователя, уже существует пользователь с почтой :" +
                    newUser.getEmail());
        }
    }

    @Override
    public void validateEmailBusy(long id, UserDto userDto) {
        if (storage.emailIsBusy(userDto.getEmail())) {

            if (storage.userByMail(userDto.getEmail()).getId() != id)
                throw new UserConflictException("Обновление пользователя, уже существует пользователь с почтой :"
                        + userDto.getEmail());
        }
    }

    @Override
    public void validateGetById(long id, String action) {

        if (storage.getById(id) == null) {
            throw new UserNotFoundException(action + " , не найден по id:" + id);
        }
    }
}
