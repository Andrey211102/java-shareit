package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemDtoMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemValidator;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage storage;
    private final ItemDtoMapper mapper;
    private final ItemValidator validator;

    @Override
    public ItemDto create(long idOwner, ItemDto newItem) {

        validator.validateCreate(idOwner, newItem);

        Item createdItem = storage.create(idOwner, mapper.fromDto(newItem));

        log.info("Добавлен новый предмет: {}", createdItem.getName());

        return mapper.toDto(createdItem);
    }

    @Override
    public ItemDto getById(long id) {

        validator.validateGetById(id);

        return mapper.toDto(storage.getById(id));
    }

    @Override
    public ItemDto update(long idOwner, long id, ItemDto itemDto) {

        validator.validateUpdate(idOwner, id, itemDto);

        Item updatedItem = storage.update(idOwner, id, itemDto);
        log.info("Обновлен предмет : {}", updatedItem.getName());

        return mapper.toDto(updatedItem);
    }

    @Override
    public List<ItemDto> getByOwner(long idOwner) {

        return storage.getByOwner(idOwner)
                .stream()
                .map((item -> mapper.toDto(item)))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String request) {

        return storage.search(request)
                .stream()
                .map((item -> mapper.toDto(item)))
                .collect(Collectors.toList());
    }
}
