package ru.practicum.stat.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ApplicationDtoForHits {
    private String app;
    private String uri;
    private Long hits;
}
