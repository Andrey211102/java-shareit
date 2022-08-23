package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    @Override
    public UserDto create(UserDto newUser) {
        return storage.create(newUser);
    }

    @Override
    public UserDto update(long id, UserDto user) {
        return storage.update(id, user);
    }

    @Override
    public UserDto getByIdDto(long id) {
        return storage.getByIdDto(id);
    }

    @Override
    public void deleteById(long id) {
        storage.delete(id);
    }

    @Override
    public List<UserDto> getAll() {
        return storage.getAll();
    }
}
