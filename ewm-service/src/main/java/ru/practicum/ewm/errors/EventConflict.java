package ru.practicum.ewm.errors;

public class EventConflict extends RuntimeException {

    public EventConflict(String message) {
        super(message);
    }
}
