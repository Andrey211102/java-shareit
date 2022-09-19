package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService service;

    @PostMapping
    public BookingInfDto create(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                @Validated({Create.class}) @RequestBody BookingDto bookingDto) {

        return service.create(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingInfDto confirmation(@RequestHeader("X-Sharer-User-Id") Long ownerItemId,
                                      @PathVariable Long bookingId,
                                      @RequestParam(value = "approved") Boolean approved) {

        return service.confirmation(ownerItemId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingInfDto getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {

        return service.getByIdAndBookerOrOwner(userId, bookingId);
    }

    @GetMapping()
    public List<BookingInfDto> getAllByState(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                             @RequestParam(value = "state", required = false, defaultValue = "ALL")
                                             String state) {
        return service.getAllByState(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingInfDto> getAllByOwnerAndState(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                     @RequestParam(value = "state", required = false,
                                                             defaultValue = "ALL") String state) {
        return service.getAllByOwnerAndState(bookerId, state);
    }
}
