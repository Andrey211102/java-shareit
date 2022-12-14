package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfDto;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService service;

    @PostMapping
    public BookingInfDto create(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                @RequestBody BookingDto bookingDto) {

        log.info("Получен POST - запрос создания нового бронирования, user id: {}", bookerId);
        return service.create(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingInfDto confirmation(@RequestHeader("X-Sharer-User-Id") Long ownerItemId,
                                      @PathVariable Long bookingId,
                                      @RequestParam(value = "approved") Boolean approved) {

        log.info("Получен PATH - запрос подтверждения бронирования id: {}, значение: {}", bookingId, approved);
        return service.confirmation(ownerItemId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingInfDto getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {

        log.info("Получен GET - запрос полученя бронирования по id: {}", bookingId);
        return service.getByIdAndBookerOrOwner(userId, bookingId);
    }

    @GetMapping()
    public List<BookingInfDto> getAllByState(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                             @RequestParam(value = "state", required = false, defaultValue = "ALL")
                                             String state,
                                             @RequestParam(name = "from", defaultValue = "0")
                                             Integer from,
                                             @RequestParam(name = "size", defaultValue = "10")
                                             Integer size) {

        log.info("Получен GET - запрос полученя списка бронирований c состоянием: {}", state);
        return service.getAllByState(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingInfDto> getAllByOwnerAndState(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                     @RequestParam(value = "state", required = false,
                                                             defaultValue = "ALL") String state,
                                                     @RequestParam(name = "from", defaultValue = "0")
                                                     Integer from,
                                                     @RequestParam(name = "size", defaultValue = "10")
                                                     Integer size) {

        log.info("Получен GET - запрос полученя списка бронирований владельца id:{}, c состоянием: {}", bookerId, state);
        return service.getAllByOwnerAndState(bookerId, state, from, size);
    }
}
