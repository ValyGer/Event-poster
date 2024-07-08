package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.ParametersForRequest;

import java.util.List;

public interface EventService {

    // Часть private
    List<EventShortDto> getAllEventOfUser(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEvenDto newEvenDto);

    EventFullDto getEventOfUserById(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    // Часть admin

    List<EventFullDto> getAllEventsByAdmin(ParametersForRequest parametersForRequest);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
