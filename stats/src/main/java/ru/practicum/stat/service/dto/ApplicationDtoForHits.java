package ru.practicum.stat.service.dto;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ApplicationDtoForHits {
    private String app;
    private String uri;
    private Long hits;
}
