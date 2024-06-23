package ru.practicum.stat.service.service;

import org.springframework.http.HttpStatus;
import ru.practicum.stat.service.dto.RecordDto;

public interface RecordService {
    HttpStatus addRecord(RecordDto recordDto);
}
