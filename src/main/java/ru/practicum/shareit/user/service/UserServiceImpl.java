package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.exceptions.UserConflictException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDtoMapper;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;
    private final UserDtoMapper mapper;

    @Override
    public UserDto create(UserDto userDto) {

        User user = storage.save(mapper.fromDto(userDto));
        log.info("Добавлен пользователь : {}", user.getName());

        return mapper.toDto(user);
    }

    @Override
    public UserDto update(long id, UserDto userDto) {

        String action = "Обновление пользователя";

        User userStor = storage.findById(id)
                .orElseThrow(() -> new UserNotFoundException(action + " , не найден по id:" + id));

        //email
        if (userDto.getEmail() != null && !userDto.getEmail().equals(userStor.getEmail())) {

            Optional<User> userByEmail = storage.findByEmailEqualsIgnoreCase(userDto.getEmail());
            if (userByEmail.isPresent()) {

                if (userByEmail.get().getId() != id) throw new UserConflictException("Обновление пользователя, " +
                        "уже существует пользователь с почтой :"
                        + userDto.getEmail());
            }
            userStor.setEmail(userDto.getEmail());
        }
        //name
        if (userDto.getName() != null) userStor.setName(userDto.getName());

        storage.save(userStor);

        log.info("Обновлен пользователь Id: {}", id);
        return mapper.toDto(userStor);
    }

    @Override
    public UserDto getById(long id) {

        User user = storage.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Получение пользователя по id , не найден по id:" + id));

        return mapper.toDto(user);
    }

    @Override
    public void deleteById(long id) {

        Optional<User> user = storage.findById(id);

        if (user.isEmpty()) throw new UserNotFoundException("Удаление пользователя, не найден по id:" + id);

        storage.deleteById(id);
        log.info("Удален пользователь с id: {}", id);
    }

    @Override
    public List<UserDto> getAll() {
        return storage.findAll()
                .stream()
                .map(user -> mapper.toDto(user))
                .collect(Collectors.toList());
    }
}

