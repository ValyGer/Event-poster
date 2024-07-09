package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.errors.DataConflictRequest;
import ru.practicum.ewm.errors.NotFoundException;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.*;
import ru.practicum.ewm.event.respository.EventRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventMapper eventMapper;

    // Часть private

    public List<EventShortDto> getAllEventOfUser(Long userId, Integer from, Integer size) {
        List<EventShortDto> eventsOfUser;
        userService.getUserById(userId); //Проверка пользователя
        List<Event> events = eventRepository.findEventsOfUser(userId, PageRequest.of(from / size, size));
        eventsOfUser = events.stream().map(eventMapper::toEventShortDto).collect(Collectors.toList());
// добавить просмотры!!!!
        log.info("Получение всех событий пользователя с ID = {}", userId);
        return eventsOfUser;
    }

    @Transactional
    public EventFullDto createEvent(Long userId, NewEvenDto newEvenDto) {
        User initiator = userService.getUserById(userId); //Проверка пользователя
        Category category = categoryService.getCategoryByIdNotMapping(newEvenDto.getCategory()); // Проверка категории
        Event event = eventMapper.toEvent(newEvenDto);

        event.setInitiator(initiator);
        event.setCategory(category);
        event.setConfirmedRequests(0L);
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());

        Event eventSave = eventRepository.save(event);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(eventSave);
        eventFullDto.setViews(0L);
        log.info("Событию присвоен ID = {}, и оно успешно добавлено", event.getId());
        return eventFullDto;
    }

    public EventFullDto getEventOfUserById(Long userId, Long eventId) {
        userService.getUserById(userId); //Проверка пользователя
        Optional<Event> optEventSaved = eventRepository.findByIdAndInitiatorId(eventId, userId);
        EventFullDto eventFullDto;
        if (optEventSaved.isPresent()) {
            eventFullDto = eventMapper.toEventFullDto(optEventSaved.get());
        } else {
            throw new DataConflictRequest("The required object was not found.");
        }
// Добавить просмотры
        log.info("Поиск события с ID = {}", eventId);
        return eventFullDto;
    }

    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        userService.getUserById(userId); //Проверка пользователя
        Optional<Event> optEventSaved = eventRepository.findByIdAndInitiatorId(eventId, userId);
        Event eventSaved;
        if (optEventSaved.isPresent()) {
            eventSaved = optEventSaved.get();
        } else {
            throw new NotFoundException("Event with ID = " + eventId + " was not found");
        }

        Event eventNew = eventMapper.toUpdateEventUserRequest(updateEventUserRequest);
        if (eventSaved.getState().equals(EventState.PUBLISHED)) {
            throw new DataConflictRequest("Only pending or canceled events can be changed");
        }
        if (eventNew.getEventDate() != null) {
            eventSaved.setEventDate((eventNew.getEventDate()));
        }
        if (updateEventUserRequest.getStateAction() != null) {
            updateStateOfEventByUser(updateEventUserRequest.getStateAction(), eventSaved);
        }
        if (eventNew.getAnnotation() != null) {
            eventSaved.setAnnotation(eventNew.getAnnotation());
        }
        if (eventNew.getCategory().getId() != 0) {
            eventSaved.setCategory(eventNew.getCategory());
        }
        if (eventNew.getDescription() != null) {
            eventSaved.setDescription(eventNew.getDescription());
        }
        if (eventNew.getLat() != null) {
            eventSaved.setLat(eventNew.getLat());
        }
        if (eventNew.getLon() != null) {
            eventSaved.setLon(eventNew.getLon());
        }
        if (eventNew.getParticipantLimit() != null) {
            eventSaved.setParticipantLimit(eventSaved.getParticipantLimit());
        }
        if (eventNew.isPaid() != eventSaved.isPaid()) {
            eventSaved.setPaid(eventNew.isPaid());
        }
        if (eventNew.isRequestModeration() != eventSaved.isRequestModeration()) {
            eventSaved.setRequestModeration(eventNew.isRequestModeration());
        }
        if (eventNew.getTitle() != null) {
            eventSaved.setTitle((eventNew.getTitle()));
        }

        Event eventUpdate = eventRepository.save(eventSaved);
// добавить статистику просмотров
        log.info("Событие ID = {} пользователя ID = {} успешно обновлено", eventId, userId);
        return eventMapper.toEventFullDto(eventUpdate);
    }


    // Часть admin

    public List<EventFullDto> getAllEventsByAdmin(ParametersForRequest parametersForRequest) {
        return null;
    }

    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Optional<Event> optEventSaved = eventRepository.findById(eventId);
        Event eventSaved;
        if (optEventSaved.isPresent()) {
            eventSaved = optEventSaved.get();
        } else {
            throw new NotFoundException("Event with ID = " + eventId + " was not found");
        }

        Event eventNew = eventMapper.toUpdateEventAdminRequest(updateEventAdminRequest);
        if (eventNew.getEventDate() != null) {
            eventSaved.setEventDate((eventNew.getEventDate()));
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            updateStateOfEventByAdmin(updateEventAdminRequest.getStateAction(), eventSaved);
        }
        if (eventNew.getAnnotation() != null) {
            eventSaved.setAnnotation(eventNew.getAnnotation());
        }
        if (eventNew.getCategory().getId() != 0) {
            eventSaved.setCategory(eventNew.getCategory());
        }
        if (eventNew.getDescription() != null) {
            eventSaved.setDescription(eventNew.getDescription());
        }
        if (eventNew.getLat() != null) {
            eventSaved.setLat(eventNew.getLat());
        }
        if (eventNew.getLon() != null) {
            eventSaved.setLon(eventNew.getLon());
        }
        if (eventNew.getParticipantLimit() != null) {
            eventSaved.setParticipantLimit(eventSaved.getParticipantLimit());
        }
        if (eventNew.isPaid() != eventSaved.isPaid()) {
            eventSaved.setPaid(eventNew.isPaid());
        }
        if (eventNew.isRequestModeration() != eventSaved.isRequestModeration()) {
            eventSaved.setRequestModeration(eventNew.isRequestModeration());
        }
        if (eventNew.getTitle() != null) {
            eventSaved.setTitle((eventNew.getTitle()));
        }

        Event eventUpdate = eventRepository.save(eventSaved);
// добавить статистику просмотров
        log.info("Событие ID = {} успешно обновлено от имени администратора", eventId);
        return eventMapper.toEventFullDto(eventUpdate);
    }

    // Часть public


    // Вспомогательная часть
    // Вспомогательная функция обновления статуса
    private void updateStateOfEventByUser(String stateAction, Event eventSaved) {
        StateActionForUser stateActionForUser;
        try {
            stateActionForUser = StateActionForUser.valueOf(stateAction);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid parameter stateAction");
        }
        switch (stateActionForUser) {
            case SEND_TO_REVIEW:
                eventSaved.setState(EventState.PENDING);
                break;
            case CANCEL_REVIEW:
                eventSaved.setState(EventState.CANCELED);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + stateAction);
        }
    }

    // Вспомогательная функция обновления статуса и время публикации
    private void updateStateOfEventByAdmin(String stateAction, Event eventSaved) {
        StateActionForAdmin stateActionForAdmin;
        try {
            stateActionForAdmin = StateActionForAdmin.valueOf(stateAction);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid parameter stateAction");
        }
        switch (stateActionForAdmin) {
            case REJECT_EVENT:
                if (eventSaved.getState().equals(EventState.PUBLISHED)) {
                    throw new IllegalArgumentException("The event has already been published.");
                }
                eventSaved.setState(EventState.CANCELED);
                break;
            case PUBLISH_EVENT:
                if (!eventSaved.getState().equals(EventState.PENDING)) {
                    throw new IllegalArgumentException("Cannot publish the event because it's not in the right state: PUBLISHED");
                }
                eventSaved.setState(EventState.PUBLISHED);
                eventSaved.setPublishedOn(LocalDateTime.now());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + stateAction);
        }
    }

    // Получение event по id
    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID = " + eventId + " was not found"));
    }

    public void addRequestToEvent(Event event) {
        eventRepository.save(event);
    }
}

