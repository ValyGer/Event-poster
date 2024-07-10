package ru.practicum.ewm.request.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.Request;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto createRequest(long userId, long eventId);

    List<ParticipationRequestDto> getAllRequestByUser(long userId);

    ParticipationRequestDto cancellationRequest(long userId, long requestId);

    Iterable<Request> findAll(BooleanExpression conditions);
}
