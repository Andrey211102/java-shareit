package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item create(long idOwner, Item item);

    Item update(long idOwner, long id, ItemDto item);

    Item getById(long id);

    List<Item> getByOwner(long idOwner);

    List<Item> search(String request);
}


