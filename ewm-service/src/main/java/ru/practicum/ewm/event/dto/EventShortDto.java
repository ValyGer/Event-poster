package ru.practicum.ewm.event.dto;

import lombok.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.service.EventServiceImpl;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.util.Comparator;

import static ru.practicum.ewm.event.model.SortType.EVENT_DATE;
import static ru.practicum.ewm.event.model.SortType.VIEWS;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private String eventDate;
    private Long id;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}


