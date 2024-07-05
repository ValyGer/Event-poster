package ru.practicum.stats;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class EndpointHit {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
