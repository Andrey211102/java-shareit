package ru.practicum.shareit.item.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemForbiddenException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.exceptions.ItemValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemDtoMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemInMemoryStorage implements ItemStorage {

    private Long lastUid = 1L;
    private final HashMap<Long, Item> items;
    private final ItemDtoMapper mapper;
    private final UserStorage userStorage;

    @Override
    public ItemDto create(long idOwner, ItemDto newItem) {

        if (idOwner <= 0)
            throw new ItemValidationException("Обновление Item, передан не корректный id владельца : " + idOwner);

        User owner = userStorage.getById(idOwner);
        if (owner == null) throw new UserNotFoundException("Обновление Item, не найден владелец по id:  " + idOwner);

        Item item = mapper.fromDto(newItem);
        item.setOwner(userStorage.getById(idOwner));
        item.setId(lastUid);
        items.put(item.getId(), item);
        log.info("Добавлен новый предмет: {}", item.getName());

        setLastUid();

        return mapper.toDto(item);
    }

    @Override
    public ItemDto update(long idOwner, long id, ItemDto updatedItem) {

        if (idOwner <= 0)
            throw new ItemValidationException("Обновление Item, передан не корректный id владельца : " + idOwner);
        if (!items.containsKey(id)) throw new ItemNotFoundException("Обновление Item, не найден по id: " + id);

        User owner = userStorage.getById(idOwner);
        if (owner == null) throw new UserNotFoundException("Обновление Item, не найден владелец по id:  " + idOwner);

        Item item = items.get(id);

        if (item.getOwner().getId() != idOwner) throw new ItemForbiddenException("Обновление Item, передан" +
                " не верный id владельца");

        //Изменение свойств
        if (updatedItem.getName() != null) item.setName(updatedItem.getName());
        if (updatedItem.getDescription() != null) item.setDescription(updatedItem.getDescription());
        if (updatedItem.getAvailable() != null) item.setAvailable(updatedItem.getAvailable());

        log.info("Обновлен предмет : {}", item.getName());

        return mapper.toDto(item);
    }

    @Override
    public ItemDto getByIdDto(long id) {
        if (!items.containsKey(id)) throw new ItemNotFoundException("Получение по Id, не найден по id: " + id);
        return mapper.toDto(items.get(id));
    }

    @Override
    public List<ItemDto> getByOwner(long idOwner) {

        return items.values()
                .stream()
                .filter((item -> item.getOwner().getId() == idOwner))
                .map((item -> mapper.toDto(item)))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String request) {

        if (request.length() == 0) return new ArrayList<>();

        String text = request.toLowerCase();

        return items.values()
                .stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text))
                .map((item -> mapper.toDto(item)))
                .collect(Collectors.toList());
    }

    private void setLastUid() {
        if (this.items.isEmpty()) this.lastUid = 1L;
        else this.lastUid++;
    }
}