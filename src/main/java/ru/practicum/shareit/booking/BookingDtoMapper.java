package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@Service
@RequiredArgsConstructor
public class BookingDtoMapper {

    public BookingInfDto toInfDto(Booking booking) {

        BookingInfDto dto = new BookingInfDto();

        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());

        dto.setBooker(new BookingInfDto
                .BookerDto(booking.getBooker().getId(), booking.getBooker().getName()));

        dto.setItem(new BookingInfDto
                .BookingItemDto(booking.getItem().getId(), booking.getItem().getName()));
        return dto;
    }

    public Booking fromDto(BookingDto dto, Item item, User booker, BookingStatus status) {

        Booking booking = new Booking();

        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);

        return booking;
    }
}
