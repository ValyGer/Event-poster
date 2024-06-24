package ru.practicum.stat.service.service;

import org.springframework.http.HttpStatus;
import ru.practicum.stat.service.dto.ApplicationDto;
import ru.practicum.stat.service.dto.RecordDto;

import java.time.LocalDateTime;
import java.util.List;

public interface RecordService {
    HttpStatus addRecord(RecordDto recordDto);

    List<ApplicationDto>  getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
