package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.stats.EndpointHit;
import ru.practicum.stats.ViewStats;
import ru.practicum.stats.exceptions.DataTimeException;
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.model.HitMapper;
import ru.practicum.stats.repository.HitRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HitServiceImpl implements HitService {

    private final HitRepository hitRepository;
    private final HitMapper hitMapper;

    @Transactional
    public HttpStatus addHit(EndpointHit endpointHit) {
        Hit hit = hitMapper.toHit(endpointHit);
        log.info("Сохранение в базу информации о запросе {}", hit);
        hitRepository.save(hit);
        return HttpStatus.OK;
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end,
                                    List<String> uris, boolean unique) {
        if (end.isBefore(start)) {
            throw new DataTimeException("Дата окончания не может быть раньше даты начала");
        }
        List<ViewStats> statistic;
        if (unique) { // Вывод статистики только для уникальных запросов
            if (uris == null || uris.isEmpty()) {
                log.info("Получение статистики уникальных запросов для серверов где URIs пустой");
                statistic = hitRepository.findAllUniqueHitsWhenUriIsEmpty(start, end);
            } else {
                log.info("Получение статистики уникальных запросов для перечисленных URIs");
                statistic = hitRepository.findAllUniqueHitsWhenUriIsNotEmpty(start, end, uris);
            }
        } else { // Вывод статистики для всех запросов
            if (uris == null || uris.isEmpty()) {
                log.info("Получение статистики без учета уникальных запросов для серверов где URIs пустой");
                statistic = hitRepository.findAllHitsWhenUriIsEmpty(start, end);
            } else {
                log.info("Получение статистики без учета уникальных запросов для перечисленных URIs");
                statistic = hitRepository.findAllHitsWhenStarEndUris(start, end, uris);
            }
        }
        return statistic;
    }
}
