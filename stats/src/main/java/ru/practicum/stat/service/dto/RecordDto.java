package ru.practicum.stat.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class RecordDto {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
