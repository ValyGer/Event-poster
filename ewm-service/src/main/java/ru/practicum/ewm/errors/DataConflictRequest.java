package ru.practicum.ewm.errors;

public class DataConflictRequest extends RuntimeException{
    public DataConflictRequest(String message) {
        super(message);
    }
}
