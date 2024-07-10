package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface EventService {

    // Часть private
    List<EventShortDto> getAllEventOfUser(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEvenDto newEvenDto);

    EventFullDto getEventOfUserById(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getRequestEventByUser(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeRequestEventStatus(Long userId, Long eventId,
                                                            EventRequestStatusUpdateRequest request);

    // Часть admin
    List<EventFullDto> getAllEventsByAdmin(EventAdminParams eventAdminParams);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);


    // Часть private
    List<EventShortDto> getAllEventsByUser(EventPublicParams eventPublicParams);

    EventFullDto getEventDtoById(Long id);


    // Вспомогательная часть
    Event getEventById(Long eventId);

    void addRequestToEvent(Event event);
}
