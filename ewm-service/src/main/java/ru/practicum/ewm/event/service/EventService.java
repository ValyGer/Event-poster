package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.NewEvenDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDtoForUpdate;

import java.util.List;

public interface EventService {
    List<EventShortDto> getAllEventOfUser(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEvenDto newEvenDto);

    EventFullDto getEventOfUserById(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, NewEventDtoForUpdate newEventDtoForUpdate);
}
