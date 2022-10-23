package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                         @Validated({Create.class}) @RequestBody BookingDto bookingDto) {

        log.info("Получен POST - запрос создания нового бронирования, user id: {}", bookerId);
        return client.create(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirmation(@RequestHeader("X-Sharer-User-Id") Long ownerItemId,
                                               @PathVariable Long bookingId,
                                               @RequestParam(value = "approved") Boolean approved) {

        log.info("Получен PATH - запрос подтверждения бронирования id: {}, значение: {}", bookingId, approved);
        return client.confirmation(ownerItemId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {

        log.info("Получен GET - запрос полученя бронирования по id: {}", bookingId);
        return client.getByIdAndBookerOrOwner(userId, bookingId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllByState(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                @RequestParam(value = "state", required = false, defaultValue = "ALL")
                                                String state,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10")
                                                Integer size) {

        log.info("Получен GET - запрос полученя списка бронирований c состоянием: {}", state);
        return client.getAllByState(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwnerAndState(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                        @RequestParam(value = "state", required = false,
                                                                defaultValue = "ALL") String state,
                                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                        Integer from,
                                                        @Positive @RequestParam(name = "size", defaultValue = "10")
                                                        Integer size) {

        log.info("Получен GET - запрос полученя списка бронирований владельца id:{}, c состоянием: {}", bookerId, state);
        return client.getAllByOwnerAndState(bookerId, state, from, size);
    }
}
