package ru.practicum.shareit.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exceptions.BookingStatusValidateExeption;
import ru.practicum.shareit.booking.exceptions.BookingValidationException;
import ru.practicum.shareit.item.ItemValidationException;
import ru.practicum.shareit.requests.ItemRequestValidationExeption;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final ItemValidationException e) {
        return new ErrorResponse("Ошибка валидации предмета", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final BookingValidationException e) {
        return new ErrorResponse("Ошибка валидации бронирования", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final BookingStatusValidateExeption e) {
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final ItemRequestValidationExeption e) {
        return new ErrorResponse("Ошибка валидации запроса добавления", e.getMessage());
    }
}
