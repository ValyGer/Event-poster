package ru.practicum.stat.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.stat.service.dto.ApplicationDtoForHits;
import ru.practicum.stat.service.dto.ApplicationDtoForHitsInt;
import ru.practicum.stat.service.dto.RecordDto;
import ru.practicum.stat.service.exceptions.DataTimeException;
import ru.practicum.stat.service.mapper.ApplicationMapper;
import ru.practicum.stat.service.mapper.RecordMapper;
import ru.practicum.stat.service.model.Application;
import ru.practicum.stat.service.model.Record;
import ru.practicum.stat.service.repository.RecordRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecordServiceImpl implements RecordService {

    private final ApplicationService applicationService;
    private final RecordRepository recordRepository;
    private final RecordMapper recordMapper;
    private final ApplicationMapper applicationMapper;

    public HttpStatus addRecord(RecordDto recordDto) {
        log.info("Выполнение проверки существования сервиса {} в базе статистики", recordDto.getApp());
        Application application = getApplicationId(recordDto.getApp());

        Record record = recordMapper.toRecord(recordDto);
        record.setApp(application);
        log.info("Сохранение в базу информации о запросе {}", record);
        Record saveRecord = recordRepository.save(record);
        System.out.println(saveRecord);
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
                statistic = recordRepository.findAllUniqueRecordsWhenUriIsEmpty(start, end);
            } else {
                log.info("Получение статистики уникальных запросов для перечисленных URIs");
                statistic = recordRepository.findAllUniqueRecordsWhenUriIsNotEmpty(start, end, uris);
            }
        } else { // Вывод статистики для всех запросов
            if (uris == null || uris.isEmpty()) {
                log.info("Получение статистики без учета уникальных запросов для серверов где URIs пустой");
                statistic = recordRepository.findAllRecordsWhenUriIsEmpty(start, end);
            } else {
                log.info("Получение статистики без учета уникальных запросов для перечисленных URIs");
                statistic = recordRepository.findAllRecordsWhenStarEndUris(start, end, uris);
            }
        }
        return statistic.stream().map(applicationMapper::toApplicationDtoForHitsInt).collect(Collectors.toList());
    }
}
