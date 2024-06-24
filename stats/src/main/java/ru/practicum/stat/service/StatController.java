package ru.practicum.stat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stat.service.dto.ApplicationDtoForHits;
import ru.practicum.stat.service.dto.ApplicationDtoForHitsInt;
import ru.practicum.stat.service.dto.RecordDto;
import ru.practicum.stat.service.service.RecordService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatController {

    private final RecordService recordService;

    @GetMapping("/stats")
    public ResponseEntity<List<ApplicationDtoForHitsInt>> getStats(
            @RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(value = "uris", required = false) List<String> uris,
            @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        log.info("Вызван метода с запросом статистики в период с {} до {}, для следующих серверов {}. " +
                "Выбраны только уникальные значения - {}", start, end, uris, unique);
        return ResponseEntity.ok(recordService.getStats(start, end, uris, unique));
    }

    @PostMapping("/hit")
    @Transactional
    public ResponseEntity<HttpStatus> addRecord(@Valid @RequestBody RecordDto recordDto) {
        log.info("Вызван метод добавления записи в статистику {}", recordDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(recordService.addRecord(recordDto));
    }
}
