package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfDto;

import java.util.List;

public interface ItemService {

    ItemDto create(long idOwner, ItemDto newItem);

    ItemDto update(long idOwner, long id, ItemDto item);

    List<ItemInfDto> getByOwner(long idOwner);

    ItemInfDto getById(long id, long userId);

    List<ItemDto> search(String request);

    CommentInfDto addComment(Long authorId, Long itemId, CommentDto commentDto);
}
