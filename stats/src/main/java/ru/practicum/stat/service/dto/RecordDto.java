package ru.practicum.stat.service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class RecordDto {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
