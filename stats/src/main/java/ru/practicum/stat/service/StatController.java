package ru.practicum.stat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.stat.service.dto.ApplicationDto;
import ru.practicum.stat.service.dto.RecordDto;
import ru.practicum.stat.service.service.RecordService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatController {

    private final RecordService recordService;

    @GetMapping("/stats")
    public ResponseEntity<List<ApplicationDto>> getStats() {
        return null;
    }

    @PostMapping("/hit")
    @Transactional
    public ResponseEntity<HttpStatus> addRecord(@Valid @RequestBody RecordDto recordDto) {
        log.info("Вызван метод добавления записи в статистику {}", recordDto);
        return ResponseEntity.ok(recordService.addRecord(recordDto));
    }
}
