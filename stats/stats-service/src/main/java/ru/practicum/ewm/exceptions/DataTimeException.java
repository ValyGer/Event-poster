package ru.practicum.ewm.exceptions;

import lombok.Getter;

@Getter
public class DataTimeException extends RuntimeException {
    public DataTimeException(String message) {
        super(message);
    }
}
