package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ApplicationDtoForHitsInt {
    private String app;
    private String uri;
    private int hits;
}
