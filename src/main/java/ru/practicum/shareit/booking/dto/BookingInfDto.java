package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BookingInfDto {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;

    private BookerDto booker;

    private BookingItemDto item;

    @AllArgsConstructor
    @Getter
    public static class BookerDto {
        Long id;
        String name;
    }

    @AllArgsConstructor
    @Getter
    public static class BookingItemDto {
        Long id;
        String name;
    }
}
