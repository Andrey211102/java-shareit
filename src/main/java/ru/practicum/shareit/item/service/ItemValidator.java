package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemValidator {

    void validateCreate(long idOwner, ItemDto newItem);

    void validateGetById(long id);

    void validateUpdate(long idOwner, long id, ItemDto itemDto);
}
