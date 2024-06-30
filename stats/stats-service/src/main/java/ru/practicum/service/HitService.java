package ru.practicum.service;

import org.springframework.http.HttpStatus;
import ru.practicum.dto.ApplicationDtoForHitsInt;
import ru.practicum.dto.HitDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {
    HttpStatus addHit(HitDto hitDto);

    List<ApplicationDtoForHitsInt> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
