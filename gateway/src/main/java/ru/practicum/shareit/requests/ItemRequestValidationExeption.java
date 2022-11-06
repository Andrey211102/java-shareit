package ru.practicum.shareit.requests;

public class ItemRequestValidationExeption extends RuntimeException {

    public ItemRequestValidationExeption(String message) {
        super(message);
    }
}
