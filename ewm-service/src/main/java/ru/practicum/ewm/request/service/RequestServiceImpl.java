package ru.practicum.ewm.request.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.errors.ConflictException;
import ru.practicum.ewm.errors.NotFoundException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.respository.EventRepository;
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
@NoArgsConstructor(force = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserService userService;
    private final EventService eventService;

    @Autowired
    @Lazy
    public RequestServiceImpl(RequestRepository requestRepository, RequestMapper requestMapper,
                              UserService userService,
                              EventService eventService) {
        this.requestRepository = requestRepository;
        this.requestMapper = requestMapper;
        this.userService = userService;
        this.eventService = eventService;
    }

    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
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
        if ((event.getParticipantLimit() == 0) || (event.getRequestModeration() == null)) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventService.addRequestToEvent(event);
        }

        request = requestRepository.save(request);
        log.info("Запрос на участие пользователя с ID = {} в событии с ID = {} успешно добавлено", userId, eventId);
        return requestMapper.toParticipationRequestDto(request);
    }

    public List<ParticipationRequestDto> getAllRequestByUser(Long userId) {
        User user = userService.getUserById(userId);
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        log.info("Поиск запросов на участие в событиях пользователя с ID = {}", userId);
        return requests.stream().map(requestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    public ParticipationRequestDto cancellationRequest(Long userId, Long requestId) {
        User user = userService.getUserById(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id = " + requestId + " was not found"));
        request.setStatus(RequestStatus.CANCELED);
        request = requestRepository.save(request);
        log.info("Бронирование пользователя с ID = {} на посещение мероприятия с ID = {} успешно отменено",
                userId, requestId);
        return requestMapper.toParticipationRequestDto(request);
    }

    public Iterable<Request> findAll(BooleanExpression conditions) {
        return requestRepository.findAll(conditions);
    }

    public List<Request> getAllByEventId(Long eventId) {
        return requestRepository.findAllByEventId(eventId);
    }

    public List<Request> getAllByRequestIdIn(List<Long> requestsId) {
        return requestRepository.findAllByIdIn(requestsId);
    }

    public List<Request> saveAll(List<Request> requests) {
        return requestRepository.saveAll(requests);
    }
}
