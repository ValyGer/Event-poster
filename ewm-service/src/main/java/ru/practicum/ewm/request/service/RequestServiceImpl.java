package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.errors.ConflictException;
import ru.practicum.ewm.errors.NotFoundException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserService userService;
    private final EventService eventService;

    public ParticipationRequestDto createRequest(long userId, long eventId) {
        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);
        List<Request> requests = requestRepository.findAllByRequesterIdAndEventId(userId, eventId);

        if (!requests.isEmpty()) {
            throw new ConflictException("You cannot add a repeat request");
        }
        if (userId == event.getInitiator().getId()) {
            throw new ConflictException("The initiator of the event cannot add a request to participate in his event");
        }
        if (!event.getState().equals((EventState.PUBLISHED))) {
            throw new ConflictException("You cannot participate in an unpublished event");
        }
        if (event.getParticipantLimit() != 0 &&
                (Long.valueOf(event.getParticipantLimit()) == event.getConfirmedRequests()))
            throw new ConflictException("You cannot add a reservation, " +
                    "the event has reached the limit of requests for participation");

        Request request = new Request(event, user);
        if ((event.getParticipantLimit() == 0) || event.isRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventService.addRequestToEvent(event);
        }

        request = requestRepository.save(request);
        log.info("Запрос на участие пользователя с ID = {} в событии с ID = {} успешно добавлено", userId, eventId);
        return requestMapper.toParticipationRequestDto(request);
    }

    public List<ParticipationRequestDto> getAllRequestByUser(long userId) {
        User user = userService.getUserById(userId);
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        log.info("Поиск запросов на участие в событиях пользователя с ID = {}", userId);
        return requests.stream().map(requestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    public ParticipationRequestDto cancellationRequest(long userId, long requestId) {
        User user = userService.getUserById(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id = " + requestId + " was not found"));
        request.setStatus(RequestStatus.CANCELED);
        request = requestRepository.save(request);
        log.info("Бронирование пользователя с ID = {} на посещение мероприятия с ID = {} успешно отменено",
                userId, requestId);
        return requestMapper.toParticipationRequestDto(request);
    }
}
