package ru.practicum.ewm.event.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParametersForRequestPublic {
    private String text;
    private List<Long> categories;
    private Boolean paid;
    private String rangeStart;
    private String rangeEnd;
    private Boolean onlyAvailable;
    private String sort;
    private Integer from;
    private Integer size;
}
