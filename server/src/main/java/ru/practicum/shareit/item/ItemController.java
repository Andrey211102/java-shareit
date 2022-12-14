package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
                          @RequestBody ItemDto itemDto) {
        return service.create(ownerId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long ownerId, @PathVariable long id,
                          @RequestBody ItemDto itemDto) {
        return service.update(ownerId, id, itemDto);
    }

    @GetMapping("/{id}")
    public ItemInfDto getByid(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getById(id, userId);
    }

    @GetMapping
    public List<ItemInfDto> getByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                       @RequestParam(name = "from", defaultValue = "0")
                                       Integer from,
                                       @RequestParam(name = "size", defaultValue = "10")
                                       Integer size) {

        return service.getByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text") String text,
                                @RequestParam(name = "from", defaultValue = "0")
                                Integer from,
                                @RequestParam(name = "size", defaultValue = "10")
                                Integer size) {

        return service.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentInfDto addComment(@RequestHeader("X-Sharer-User-Id") Long authorId, @PathVariable long itemId,
                                    @RequestBody CommentDto commentDto) {
        return service.addComment(authorId, itemId, commentDto);
    }
}
