package ru.practicum.exception;

public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(String data, Long id) {
        super(data + " with id=" + id + " was not found");
    }
}
