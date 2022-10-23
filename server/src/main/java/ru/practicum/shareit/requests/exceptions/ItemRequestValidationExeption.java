package ru.practicum.shareit.requests.exceptions;

public class ItemRequestValidationExeption extends RuntimeException {

    public ItemRequestValidationExeption(String message) {
        super(message);
    }
}
