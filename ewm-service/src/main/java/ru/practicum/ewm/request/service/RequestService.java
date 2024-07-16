package ru.practicum.ewm.request.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.Request;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getAllRequestByUser(Long userId);

    ParticipationRequestDto cancellationRequest(Long userId, Long requestId);

    Iterable<Request> findAll(BooleanExpression conditions);

    List<Request> getAllByEventId(Long eventId);

    List<Request> getAllByRequestIdIn(List<Long> requestsId);

    List<Request> saveAll(List<Request> requests);
}
