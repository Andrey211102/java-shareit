package ru.practicum.shareit.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exceptions.BoockingStateException;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.item.exceptions.ItemAddCommentException;
import ru.practicum.shareit.item.exceptions.ItemForbiddenException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.requests.exceptions.ItemRequestNotFoundExeption;
import ru.practicum.shareit.user.exceptions.UserConflictException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) //Тесты требуют код 400, а состояние узнать можно только на сервере
    public ErrorResponse handle(final BoockingStateException e) {
        return new ErrorResponse("Ошибка состояния бронирования", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final BookingNotFoundException e) {
        return new ErrorResponse("Ошибка поиска бронирования", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handle(final ItemForbiddenException e) {
        return new ErrorResponse("Ошибка доступа к предмету", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) //Тесты требуют код 400 , проверка на то , что пользователь брал предмет
    public ErrorResponse handle(final ItemAddCommentException e) {
        return new ErrorResponse("Ошибка добавления комментария", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final ItemNotFoundException e) {
        return new ErrorResponse("Ошибка поиска предмета", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final ItemRequestNotFoundExeption e) {
        return new ErrorResponse("Ошибка поиска запроса добавления предмета", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handle(final UserConflictException e) {
        return new ErrorResponse("Конфликт данных пользователя", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final UserNotFoundException e) {
        return new ErrorResponse("Ошибка поиска пользователя", e.getMessage());
    }
}
