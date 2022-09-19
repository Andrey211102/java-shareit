package ru.practicum.shareit.booking.exceptions;

public class BookingForbiddenException extends RuntimeException {
    public BookingForbiddenException(String message) {
        super(message);
    }
}
