package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemStorage {

    ItemDto create(long idOwner, ItemDto newItem);

    ItemDto update(long idOwner, long id, ItemDto item);

    ItemDto getByIdDto(long id);

    List<ItemDto> getByOwner(long idOwner);

    List<ItemDto> search(String request);
}


