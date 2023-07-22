package ru.practicum.shareit.request.exception;

public class RequestValidationException extends RuntimeException {
    public RequestValidationException(String message) {
        super(message);
    }
}
