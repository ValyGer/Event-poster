package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.stats.StatClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class EventStatisticService {

    private final StatClient statClient;

    public Map<Long, Long> getEventsVies(List<Long> eventsId) {
        Map<Long, Long> eventsVies = new HashMap<>();
        if (eventsId == null || eventsId.isEmpty()) {
            return eventsVies;
        }

        List<String> uris = new ArrayList<>();
        for (Long eventId : eventsId) {
            uris.add("EVENT_URI" + eventId);
        }

        ResponseEntity<Object> response = statClient.getStats(
                LocalDateTime.now().minusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                uris,
                true
        );

        if (response.getBody() != null) {
// обработка json
        }
        return eventsVies;
    }
}
