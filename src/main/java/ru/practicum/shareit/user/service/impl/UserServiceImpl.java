package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserDtoMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserValidator;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;
    private final UserDtoMapper mapper;
    private final UserValidator validator;

    @Override
    public UserDto create(UserDto newUser) {

        validator.validateCreate(newUser);

        User user = storage.create(mapper.fromDto(newUser));
        log.info("Добавлен пользователь : {}", user.getName());

        return mapper.toDto(user);
    }

    @Override
    public UserDto update(long id, UserDto userDto) {

        validator.validateGetById(id, "Обновление пользователя");
        validator.validateEmailBusy(id, userDto);

        User updatedUser = storage.update(id, userDto);

        log.info("Обновлен пользователь Id: {}", id);

        return mapper.toDto(updatedUser);
    }

    @Override
    public UserDto getById(long id) {

        validator.validateGetById(id, "Получение пользователя по id");

        return mapper.toDto(storage.getById(id));
    }

    @Override
    public void deleteById(long id) {
        validator.validateGetById(id, "Удаление пользователя");
        storage.delete(id);
        log.info("Удален пользователь с id: {}", id);
    }

    @Override
    public List<UserDto> getAll() {
        return storage.getAll()
                .stream()
                .map(user -> mapper.toDto(user))
                .collect(Collectors.toList());
    }
}
