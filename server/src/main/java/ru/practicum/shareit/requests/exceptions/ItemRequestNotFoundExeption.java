package ru.practicum.shareit.requests.exceptions;

public class ItemRequestNotFoundExeption extends RuntimeException {

    public ItemRequestNotFoundExeption(String message) {
        super(message);
    }
}
