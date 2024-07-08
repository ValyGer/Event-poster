package ru.practicum.ewm.event.dto;

import lombok.*;
import ru.practicum.ewm.event.model.Location;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDtoForUpdate {

    @Size(min = 20, max = 2000, message = "The annotation field must be between 20 and 2000 characters long.")
    private String annotation;
    @Positive
    private Long category;
    @Size(min = 20, max = 7000, message = "The description field must be between 20 and 7000 characters long.")
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    @Size(min = 3, max = 120, message = "The title field must be between 3 and 120 characters long.")
    private String title;
}