package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemForbiddenException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.exceptions.ItemValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemValidator;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
@RequiredArgsConstructor
public class ItemValidatorImpl implements ItemValidator {

    private final UserStorage userStorage;
    private final ItemStorage storage;

    @Override
    public void validateCreate(long idOwner, ItemDto newItem) {

        if (idOwner <= 0)
            throw new ItemValidationException("Обновление Item, передан не корректный id владельца : " + idOwner);

        if (userStorage.getById(idOwner) == null) throw new UserNotFoundException("Обновление Item, " +
                "не найден владелец по id:  " + idOwner);
    }

    @Override
    public void validateGetById(long id) {
        if (storage.getById(id) == null) throw new ItemNotFoundException("Получение по Id, не найден по id: " + id);
    }

    @Override
    public void validateUpdate(long idOwner, long id, ItemDto itemDto) {

        if (idOwner <= 0) throw new ItemValidationException("Обновление Item, передан не корректный id владельца : "
                + idOwner);

        Item oldItem = storage.getById(id);

        if (oldItem == null) throw new ItemNotFoundException("Обновление Item, не найден по id: " + id);

        if (userStorage.getById(idOwner) == null) throw new UserNotFoundException("Обновление Item, " +
                "не найден владелец по id:  " + idOwner);

        if (oldItem.getOwner().getId() != idOwner) throw new ItemForbiddenException("Обновление Item, передан" +
                " не верный id владельца");
    }
}
