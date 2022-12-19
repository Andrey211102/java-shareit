package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestInfDto;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    public ItemRequestInfDto create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemRequestDto dto) {
        log.info("Получен POST - запрос. Создания запроса предмета, user id: {}", userId);
        return service.create(userId, dto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestInfDto getByid(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long requestId) {
        log.info("Получен GET - запрос. Получение запроса предмета по id: {}", requestId);
        return service.getById(userId, requestId);
    }

    @GetMapping()
    public List<ItemRequestInfDto> getByUserid(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(name = "from", defaultValue = "0")
                                               Integer from,
                                               @RequestParam(name = "size", defaultValue = "10")
                                               Integer size) {

        log.info("Получен GET - запрос. Получение запросов предмета по id пользователя: {}", userId);
        return service.getByUserid(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestInfDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("Получен GET - запрос. Получение запросов предметов пользователей");
        return service.getAll(userId, from, size);
    }
}
