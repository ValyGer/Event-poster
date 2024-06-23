package ru.practicum.stat.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.stat.service.dto.RecordDto;
import ru.practicum.stat.service.mapper.RecordMapper;
import ru.practicum.stat.service.model.Application;
import ru.practicum.stat.service.model.Record;
import ru.practicum.stat.service.repository.RecordRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecordServiceImpl implements RecordService {

    private final ApplicationService applicationService;
    private final RecordRepository recordRepository;
    private final RecordMapper recordMapper;

    public HttpStatus addRecord(RecordDto recordDto) {
        Application application = getApplicationId(recordDto.getApp());
        Record record = recordMapper.toRecord(recordDto);
        record.setApp(application);
        recordRepository.save(record);
        return HttpStatus.OK;
    }

    private Application getApplicationId(String app) {
        return applicationService.fineByName(app).orElseGet(() -> applicationService.add(app).get());
    }
}
