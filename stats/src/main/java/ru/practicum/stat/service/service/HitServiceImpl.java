package ru.practicum.stat.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.stat.service.dto.ApplicationDtoForHits;
import ru.practicum.stat.service.dto.ApplicationDtoForHitsInt;
import ru.practicum.stat.service.dto.HitDto;
import ru.practicum.stat.service.exceptions.DataTimeException;
import ru.practicum.stat.service.mapper.ApplicationMapper;
import ru.practicum.stat.service.mapper.HitMapper;
import ru.practicum.stat.service.model.Application;
import ru.practicum.stat.service.model.Hit;
import ru.practicum.stat.service.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HitServiceImpl implements HitService {

    private final ApplicationService applicationService;
    private final HitRepository hitRepository;
    private final HitMapper hitMapper;
    private final ApplicationMapper applicationMapper;

    public HttpStatus addHit(HitDto hitDto) {
        log.info("Выполнение проверки существования сервиса {} в базе статистики", hitDto.getApp());
        Application application = getApplicationId(hitDto.getApp());

        Hit hit = hitMapper.toHit(hitDto);
        hit.setApp(application);
        log.info("Сохранение в базу информации о запросе {}", hit);
        Hit saveHit = hitRepository.save(hit);
        System.out.println(saveHit);
        return HttpStatus.OK;
    }

    private Application getApplicationId(String app) {
        return applicationService.fineByName(app).orElseGet(() -> applicationService.add(app).get());
    }

    public List<ApplicationDtoForHitsInt> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (end.isBefore(start)) {
            throw new DataTimeException("Дата окончания не может быть раньше даты начала");
        }
        List<ApplicationDtoForHits> statistic;
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
        return statistic.stream().map(applicationMapper::toApplicationDtoForHitsInt).collect(Collectors.toList());
    }
}
