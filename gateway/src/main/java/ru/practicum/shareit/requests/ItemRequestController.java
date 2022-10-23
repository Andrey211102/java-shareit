package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final RequestsClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Valid ItemRequestDto dto) {
        log.info("Получен POST - запрос. Создания запроса предмета, user id: {}", userId);

        return client.create(userId, dto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getByid(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long requestId) {
        log.info("Получен GET - запрос. Получение запроса предмета по id: {}", requestId);

        return client.getById(userId, requestId);
    }

    @GetMapping()
    public ResponseEntity<Object> getByUserid(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                              Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10")
                                              Integer size) {

        log.info("Получен GET - запрос. Получение запросов предмета по id пользователя: {}", userId);

        return client.getByUserid(userId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("Получен GET - запрос. Получение запросов предметов пользователей");

        return client.getAll(userId, from, size);
    }
}
