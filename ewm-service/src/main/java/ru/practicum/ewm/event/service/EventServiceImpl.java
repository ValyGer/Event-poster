package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
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
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.RequestMapper;
import ru.practicum.ewm.request.model.QRequest;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.service.RequestService;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final RequestService requestService;
    private final CategoryService categoryService;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;

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

    public List<ParticipationRequestDto> getRequestEventByUser(Long userId, Long eventId) {
        userService.getUserById(userId);
        getEventById(eventId);
        List<Request> requests = requestService.getAllByEventId(eventId);
        log.info("Получен список заявок на участие в событии с ID = {} пользователя с ID = {}", eventId, userId);
        return requests.stream().map(requestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    public EventRequestStatusUpdateResult changeRequestEventStatus(Long userId, Long eventId,
                                                                   EventRequestStatusUpdateRequest request) {
        return null;
    }


    // Часть admin

    public List<EventFullDto> getAllEventsByAdmin(EventAdminParams eventAdminParams) {
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

    public List<EventShortDto> getAllEventsByUser(EventPublicParams request) {

        //формируем условие выборки
        BooleanExpression conditions = makeEventsQueryConditionsForPublic(request);

        //настройка размера страницы и типа сортировки
        PageRequest pageRequest = PageRequest.of(
                request.getFrom() / request.getSize(), request.getSize());

        //запрашиваем события из базы
        List<Event> events = eventRepository.findAll(conditions, pageRequest).toList();

        //запрашиваем количество одобренных заявок на участие в каждом событии
        Map<Long, Long> eventToRequestsCount = getEventRequests(events);

        //Запрашиваем количество просмотров каждого события
        //       ----------------------------------------------

        List<EventShortDto> eventsShortDto = events.stream().map(eventMapper::toEventShortDto).collect(Collectors.toList());
        for (EventShortDto eventShortDto : eventsShortDto) {
            eventShortDto.setConfirmedRequests(eventToRequestsCount.get(eventShortDto.getId()));
        }

        if (request.getSort().equals(SortType.VIEWS.toString())) {
            return eventsShortDto.stream().sorted(new EventSortByViews()).collect(Collectors.toList());
        } else {
            return eventsShortDto.stream().sorted(new EventSortByEventDate()).collect(Collectors.toList());
        }
    }

    public EventFullDto getEventDtoById(Long id) {
        Event event = getEventById(id);
        return eventMapper.toEventFullDto(event);
    }


    // ----- Вспомогательная часть ----
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

    // Собираем условие по которому будем выбирать события из базы данных
    private static BooleanExpression makeEventsQueryConditionsForPublic(EventPublicParams request) {
        QEvent event = QEvent.event;

        List<BooleanExpression> conditions = new ArrayList<>();

        // поиск фрагмента текста в аннотации, описании и заголовке
        if (!request.getText().isBlank()) {
            String textToSearch = request.getText();
            conditions.add(
                    event.title.containsIgnoreCase(textToSearch)
                            .or(event.annotation.containsIgnoreCase(textToSearch))
                            .or(event.description.containsIgnoreCase(textToSearch))
            );
        }

        // фильтрация по списку категорий
        if (!request.getCategories().isEmpty()) {
            conditions.add(
                    event.category.id.in(request.getCategories())
            );
        }

        // фильтрация по флагу платное/бесплатное событие
        if (request.getPaid() != null) {
            conditions.add(
                    event.paid.eq(request.getPaid())
            );
        }

        // фильтрация по временному диапазону, если не указано начало, то выборку производим начиная с настоящего
        // времени только в будущее
        LocalDateTime rangeStart;
        if (request.getRangeStart() != null) {
            rangeStart = request.getRangeStart();
        } else {
            rangeStart = LocalDateTime.now();
        }
        conditions.add(
                event.eventDate.goe(rangeStart)
        );

        if (request.getRangeEnd() != null) {
            conditions.add(
                    event.eventDate.loe(request.getRangeEnd())
            );
        }

        // фильтрация событий удовлетворяющих данному состоянию
        if (request.getState() != null) {
            conditions.add(
                    QEvent.event.state.in(request.getState())
            );
        }

        return conditions
                .stream()
                .reduce(BooleanExpression::and)
                .get();
    }

    // Собираем количество одобренных заявок для каждого события
    private Map<Long, Long> getEventRequests(List<Event> events) {
        QRequest request = QRequest.request;

        BooleanExpression condition = request.status.eq(RequestStatus.CONFIRMED).and(request.event.in(events));

        Iterable<Request> reqs = requestService.findAll(condition);
        return StreamSupport
                .stream(reqs.spliterator(), false)
                .collect(Collectors.groupingBy(r -> r.getEvent().getId(), Collectors.counting()));
    }

    public static class EventSortByViews implements Comparator<EventShortDto> {

        @Override
        public int compare(EventShortDto o1, EventShortDto o2) {
            return Long.compare(o1.getViews(), o2.getViews());
        }
    }

    public static class EventSortByEventDate implements Comparator<EventShortDto> {

        @Override
        public int compare(EventShortDto o1, EventShortDto o2) {
            return o1.getEventDate().compareTo(o2.getEventDate());
        }
    }
}


