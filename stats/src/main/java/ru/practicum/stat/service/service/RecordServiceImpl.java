package ru.practicum.stat.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.stat.service.dto.ApplicationDto;
import ru.practicum.stat.service.dto.RecordDto;
import ru.practicum.stat.service.mapper.RecordMapper;
import ru.practicum.stat.service.model.Application;
import ru.practicum.stat.service.model.Record;
import ru.practicum.stat.service.repository.RecordRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecordServiceImpl implements RecordService {

    private final ApplicationService applicationService;
    private final RecordRepository recordRepository;
    private final RecordMapper recordMapper;

    public HttpStatus addRecord(RecordDto recordDto) {
        log.info("Выполнение проверки существования сервиса {} в базе статистики", recordDto.getApp());
        Application application = getApplicationId(recordDto.getApp());

        Record record = recordMapper.toRecord(recordDto);
        record.setApp(application);
        log.info("Сохранение в базу информации о запросе {}", record);
        recordRepository.save(record);
        return HttpStatus.OK;
    }

    private Application getApplicationId(String app) {
        return applicationService.fineByName(app).orElseGet(() -> applicationService.add(app).get());
    }

    public List<ApplicationDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return null;


//        List<StatWithHits> result;
//
//        if (end.isBefore(start)) {
//            throw new DataException("Дата окончания не может быть раньше даты начала");
//        }
//        if (unique) {
//            if (uris == null || uris.isEmpty()) {
//                log.info("Получение статистики: в запросе эндпоинтов нет, unique = true");
//                result = statRepository.findAllUniqueWhenUriIsEmpty(start, end);
//            } else {
//                log.info("Получение статистики: в запросе эндпоинты есть, unique = true");
//                result = statRepository.findAllUniqueWhenUriIsNotEmpty(start, end, uris);
//            }
//        } else {
//            if (uris == null || uris.isEmpty()) {
//                log.info("Получение статистики: в запросе эндпоинтов нет, unique = false");
//                result = statRepository.findAllWhenUriIsEmpty(start, end);
//            } else {
//                log.info("Получение статистики: в запросе эндпоинты есть, unique = false");
//                result = statRepository.findAllWhenStarEndUris(start, end, uris);
//            }
//        }
//
//        return result.stream().map(statMapper::mapToDtoForView).collect(Collectors.toList());
//    }
    }

}
