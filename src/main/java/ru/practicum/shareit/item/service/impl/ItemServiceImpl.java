package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage storage;

    @Override
    public ItemDto create(long idOwner, ItemDto newItem) {
        return storage.create(idOwner, newItem);
    }

    @Override
    public ItemDto getByIdDto(long id) {
        return storage.getByIdDto(id);
    }

    @Override
    public ItemDto update(long idOwner, long id, ItemDto item) {
        return storage.update(idOwner, id, item);
    }

    @Override
    public List<ItemDto> getByOwner(long idOwner) {
        return storage.getByOwner(idOwner);
    }

    @Override
    public List<ItemDto> search(String request) {
        return storage.search(request);
    }
}
