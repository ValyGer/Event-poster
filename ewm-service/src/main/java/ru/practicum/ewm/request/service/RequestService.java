package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto createRequest(long userId, long eventId);
    List<ParticipationRequestDto> getAllRequestByUser(long userId);
    ParticipationRequestDto updateRequest(long userId, long requestId);
}
