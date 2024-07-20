package ru.practicum.ewm.event.service;

import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

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


    // Часть public
    List<EventShortDto> getAllEventsByUser(EventPublicParams eventPublicParams, HttpServletRequest httpServletRequest);

    EventFullDto getEventDtoById(Long id, HttpServletRequest httpServletRequest);


    // Вспомогательная часть
    Event getEventById(Long eventId);

    void addRequestToEvent(Event event);

    List<Event> getAllEventsByListId(List<Long> eventsId);

    Optional<Event> findByCategory(Category category);
}
