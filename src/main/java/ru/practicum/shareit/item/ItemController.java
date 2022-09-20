package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfDto;
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
    public ItemInfDto getByid(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getById(id, userId);
    }

    @GetMapping
    public List<ItemInfDto> getByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return service.getByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getById(@RequestParam(name = "text") String request) {
        return service.search(request);
    }

    @PostMapping("/{itemId}/comment")
    public CommentInfDto addComment(@RequestHeader("X-Sharer-User-Id") Long authorId, @PathVariable long itemId,
                                    @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        return service.addComment(authorId, itemId, commentDto);
    }
}
