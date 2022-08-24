package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.Create;
import ru.practicum.shareit.item.Update;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long ownerId,
                          @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return service.create(ownerId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long ownerId, @PathVariable long id,
                          @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        return service.update(ownerId, id, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDto getByid(@PathVariable long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<ItemDto> getByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return service.getByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getById(@RequestParam(name = "text") String request) {
        return service.search(request);
    }
}
