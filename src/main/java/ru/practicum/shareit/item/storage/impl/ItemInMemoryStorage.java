package ru.practicum.shareit.item.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
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
    private final UserStorage userStorage;

    @Override
    public Item create(long idOwner, Item item) {

        item.setOwner(userStorage.getById(idOwner));
        item.setId(lastUid);
        items.put(item.getId(), item);

        setLastUid();

        return item;
    }

    @Override
    public Item update(long idOwner, long id, ItemDto itemDto) {

        Item item = items.get(id);

        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());

        return item;
    }

    @Override
    public Item getById(long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getByOwner(long idOwner) {

        return items.values()
                .stream()
                .filter((item -> item.getOwner().getId() == idOwner))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String request) {

        if (request.length() == 0) return new ArrayList<>();

        String text = request.toLowerCase();

        return items.values()
                .stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text))
                .collect(Collectors.toList());
    }

    private void setLastUid() {
        if (this.items.isEmpty()) this.lastUid = 1L;
        else this.lastUid++;
    }
}