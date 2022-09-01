package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(long idOwner, ItemDto newItem);

    ItemDto update(long idOwner, long id, ItemDto item);

    List<ItemDto> getByOwner(long idOwner);

    ItemDto getById(long id);

    List<ItemDto> search(String request);
}
