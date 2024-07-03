package ru.practicum.stats.service;

import org.springframework.http.HttpStatus;
import ru.practicum.stats.EndpointHit;
import ru.practicum.stats.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {
    HttpStatus addHit(EndpointHit endpointHit);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
