package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestInfDto;
import ru.practicum.shareit.requests.exceptions.ItemRequestValidationExeption;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    public ItemRequestInfDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @Validated({Create.class}) @RequestBody ItemRequestDto dto) {
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
                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                               Integer from,
                                               @Positive @RequestParam(name = "size", defaultValue = "10")
                                               Integer size) {

        if (from < 0) throw new ItemRequestValidationExeption("Получен не корректный параметр from: " + from);
        int page = from / size;
        PageRequest request = PageRequest.of(page, size);

        log.info("Получен GET - запрос. Получение запросов предмета по id пользователя: {}", userId);
        return service.getByUserid(userId, request);
    }

    @GetMapping("/all")
    public List<ItemRequestInfDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("Получен GET - запрос. Получение запросов предметов пользователей");
        if (from < 0) throw new ItemRequestValidationExeption("Получен не корректный параметр from: " + from);

        int page = from / size;
        PageRequest request = PageRequest.of(page, size);

        return service.getAll(userId, request);
    }
}
