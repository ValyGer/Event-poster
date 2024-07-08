package ru.practicum.ewm.event.dto;

import lombok.*;
import ru.practicum.ewm.user.dto.UserShortDto;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
    private String annotation;
    private String category;
    private Long confirmedRequests;
    private String eventDate;
    private Long id;
    private UserShortDto initiator;
    private boolean paid;
    private String title;
    private Long views;
}


