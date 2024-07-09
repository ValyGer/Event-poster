package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.repository.RequestRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    public ParticipationRequestDto createRequest(long userId, long eventId) {
        return null;
    }

    public List<ParticipationRequestDto> getAllRequestByUser(long userId) {
        return null;
    }

    public ParticipationRequestDto updateRequest(long userId, long requestId) {
        return null;
    }
}
