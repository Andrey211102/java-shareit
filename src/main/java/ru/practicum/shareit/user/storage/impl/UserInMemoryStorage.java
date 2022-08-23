package ru.practicum.shareit.user.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserConflictException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserDtoMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserInMemoryStorage implements UserStorage {

    private final HashMap<Long, User> users;
    private final HashMap<String, User> emails;
    private final UserDtoMapper mapper;

    private Long lastUid = 1L;

    @Override
    public UserDto create(UserDto newUser) {

        if (emails.containsKey(newUser.getEmail())) {
            throw new UserConflictException("Cоздание пользователя, уже существует пользователь с почтой :" +
                    newUser.getEmail());
        }

        User user = mapper.fromDto(newUser);
        user.setId(lastUid);
        users.put(lastUid, user);
        emails.put(user.getEmail(), user);

        log.info("Добавлен пользователь : {}", user.getName());
        setLastUid();

        return mapper.toDto(user);
    }

    @Override
    public UserDto update(long id, UserDto userDto) {

        if (!users.containsKey(id)) throw new UserNotFoundException("Обновление пользователя, не найден по id:" + id);

        //Проверка на обновления на занятый email
        if (emails.containsKey(userDto.getEmail())) {

            User userByMail = emails.get(userDto.getEmail());

            if (userByMail.getId() != id) throw new UserConflictException("Обновление пользователя, " +
                    "уже существует пользователь с почтой :" + userDto.getEmail());
        }
        //Модификация
        User oldUser = users.get(id);

        if (userDto.getName() != null) oldUser.setName(userDto.getName());

        if (userDto.getEmail() != null) {
            emails.remove(oldUser.getEmail());
            oldUser.setEmail(userDto.getEmail());
            emails.put(oldUser.getEmail(), oldUser);
        }
        users.put(id, oldUser);
        log.info("Обновлен пользователь Id: {}", id);

        return mapper.toDto(oldUser);
    }

    @Override
    public UserDto getByIdDto(long id) {

        if (!users.containsKey(id)) throw new UserNotFoundException("Получение пользователя, не найден по id:" + id);
        return mapper.toDto(users.get(id));
    }

    @Override
    public User getById(long id) {

        if (!users.containsKey(id)) throw new UserNotFoundException("Получение пользователя, не найден по id:" + id);
        return users.get(id);
    }

    @Override
    public void delete(long id) {

        if (!users.containsKey(id)) throw new UserNotFoundException("Удаление пользователя, не найден по id:" + id);

        emails.remove(users.get(id).getEmail());
        users.remove(id);
        log.info("Удален пользователь с id: {}", id);
    }

    @Override
    public List<UserDto> getAll() {

        return users.values()
                .stream()
                .map(user -> mapper.toDto(user))
                .collect(Collectors.toList());
    }

    private void setLastUid() {
        if (this.users.isEmpty()) this.lastUid = 1L;
        else this.lastUid++;
    }
}
