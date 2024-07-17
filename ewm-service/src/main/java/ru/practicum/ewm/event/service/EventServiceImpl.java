package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.errors.ConflictException;
import ru.practicum.ewm.errors.DataConflictRequest;
import ru.practicum.ewm.errors.InvalidRequestException;
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

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@NoArgsConstructor(force = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final RequestService requestService;
    private final CategoryService categoryService;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final EventStatisticService eventStatisticService;

    @Autowired
    @Lazy
    public EventServiceImpl(EventRepository eventRepository, UserService userService, RequestService requestService,
                            CategoryService categoryService, EventMapper eventMapper, RequestMapper requestMapper,
                            EventStatisticService eventStatisticService) {
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.requestService = requestService;
        this.categoryService = categoryService;
        this.eventMapper = eventMapper;
        this.requestMapper = requestMapper;
        this.eventStatisticService = eventStatisticService;
    }

    // Часть private

    public List<EventShortDto> getAllEventOfUser(Long userId, Integer from, Integer size) {
        List<EventShortDto> eventsOfUser;
        userService.getUserById(userId); //Проверка пользователя
        List<Event> events = eventRepository.findEventsOfUser(userId, PageRequest.of(from / size, size));
        eventsOfUser = events.stream().map(eventMapper::toEventShortDto).collect(Collectors.toList());
        //Получаем и добавляем просмотры
        Map<Long, Long> views = eventStatisticService.getEventsViews(events.stream()
                .map(Event::getId)
                .collect(Collectors.toList()));
        for (EventShortDto eventShortDto : eventsOfUser) {
            eventShortDto.setViews(views.get(eventShortDto.getId()));
        }
        //
        log.info("Получение всех событий пользователя с ID = {}", userId);
        return eventsOfUser;
    }

    @Transactional
    public EventFullDto createEvent(Long userId, NewEvenDto newEvenDto) {
        User initiator = userService.getUserById(userId); //Проверка пользователя
        Category category = categoryService.getCategoryByIdNotMapping(newEvenDto.getCategory()); // Проверка категории
        Event event = eventMapper.toEvent(newEvenDto);

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidRequestException("The date and time for which the event is scheduled cannot be earlier " +
                    "than two hours from the current moment");
        }

        event.setInitiator(initiator);
        event.setCategory(category);
        event.setConfirmedRequests(0L);
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());

        event = eventRepository.save(event);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
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

        //Получаем и добавляем просмотры
        Map<Long, Long> views = eventStatisticService.getEventsViews(List.of(eventFullDto).stream()
                .map(EventFullDto::getId)
                .collect(Collectors.toList()));

        eventFullDto.setViews(views.get(eventFullDto.getId()));
        //

        log.info("Поиск события с ID = {}", eventId);
        return eventFullDto;
    }

    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEvent) {
        userService.getUserById(userId); //Проверка пользователя

        Optional<Event> optEventSaved = eventRepository.findByIdAndInitiatorId(eventId, userId);
        Event eventSaved;
        if (optEventSaved.isPresent()) {
            eventSaved = optEventSaved.get();
        } else {
            throw new NotFoundException("Event with ID = " + eventId + " was not found");
        }
        if (updateEvent.getEventDate() != null) {
            updateEventData(updateEvent.getEventDate(), eventSaved);
        }
        if (updateEvent.getStateAction() != null) {
            updateStateOfEventByUser(updateEvent.getStateAction(), eventSaved);
        }

        if (updateEvent.getAnnotation() != null) {
            eventSaved.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            Category category = categoryService.getCategoryByIdNotMapping(updateEvent.getCategory());
            eventSaved.setCategory(category);
        }
        if (updateEvent.getDescription() != null) {
            eventSaved.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getLocation() != null) {
            eventSaved.setLat(updateEvent.getLocation().getLat());
            eventSaved.setLon(updateEvent.getLocation().getLon());
        }
        if (updateEvent.getParticipantLimit() != null) {
            eventSaved.setParticipantLimit(eventSaved.getParticipantLimit());
        }
        if (updateEvent.getPaid() != null) {
            eventSaved.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getRequestModeration() != null) {
            eventSaved.setRequestModeration(updateEvent.getRequestModeration());
        }
        if (updateEvent.getTitle() != null) {
            eventSaved.setTitle((updateEvent.getTitle()));
        }

        Event eventUpdate = eventRepository.save(eventSaved);

        //Получаем и добавляем просмотры
        Map<Long, Long> views = eventStatisticService.getEventsViews(List.of(eventUpdate).stream()
                .map(Event::getId)
                .collect(Collectors.toList()));

        EventFullDto eventFullDto = eventMapper.toEventFullDto(eventUpdate);
        eventFullDto.setViews(views.get(eventFullDto.getId()));

        log.info("Событие ID = {} пользователя ID = {} успешно обновлено", eventId, userId);
        return eventFullDto;
    }

    public List<ParticipationRequestDto> getRequestEventByUser(Long userId, Long eventId) {
        userService.getUserById(userId);
        getEventById(eventId);
        List<Request> requests = requestService.getAllByEventId(eventId);


        log.info("Получен список заявок на участие в событии с ID = {} пользователя с ID = {}", eventId, userId);
        return requests.stream().map(requestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Transactional
    public EventRequestStatusUpdateResult changeRequestEventStatus(Long userId, Long eventId,
                                                                   EventRequestStatusUpdateRequest requestUpdate) {
        User user = userService.getUserById(userId);
        Event event = getEventById(eventId);
        if (event.getInitiator().getId() != userId) {
            throw new RuntimeException("Пользователь с ID = " + userId + " не является инициатором события с ID = " + eventId);
        }

        List<Request> requests = requestService.getAllByRequestIdIn(requestUpdate.getRequestIds()); // получаем список запросов на одобрение
        RequestStatus newStatus = RequestStatus.valueOf(requestUpdate.getStatus()); // получаем значение нового статуса
        Integer countOfRequest = requestUpdate.getRequestIds().size(); // находим количество новых заявок на одобрение

        for (Request request : requests) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConflictException("Изменить статус можно только у ожидающей подтверждения заявки на " +
                        "участие");
            }
        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        switch (newStatus) {
            case CONFIRMED:
                if ((event.getParticipantLimit() == 0) ||
                        ((event.getConfirmedRequests() + countOfRequest.longValue()) < event.getParticipantLimit()) ||
                        (!event.getRequestModeration())) {
                    requests.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
                    event.setConfirmedRequests(event.getConfirmedRequests() + countOfRequest);
                    for (Request request : requests) {
                        confirmedRequests.add(requestMapper.toParticipationRequestDto(request));
                    }
                } else if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
                    throw new ConflictException("Достигнут лимит по заявкам на данное событие");
                } else {
                    for (Request request : requests) {
                        if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                            request.setStatus(RequestStatus.CONFIRMED);
                            event.setConfirmedRequests(event.getConfirmedRequests() + 1L);
                            confirmedRequests.add(requestMapper.toParticipationRequestDto(request));
                        } else {
                            request.setStatus(RequestStatus.REJECTED);
                            rejectedRequests.add(requestMapper.toParticipationRequestDto(request));
                        }
                    }
                }
                break;
            case REJECTED:
                for (Request request : requests) {
                    request.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(requestMapper.toParticipationRequestDto(request));
                }
                break;
        }
        eventRepository.save(event);
        requestService.saveAll(requests);
        log.info("Статусы заявок успешно обновлены");
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }


    // Часть admin

    public List<EventFullDto> getAllEventsByAdmin(EventAdminParams eventAdminParams) {

        //формируем условие выборки
        BooleanExpression conditions = makeEventsQueryConditionsForAdmin(eventAdminParams);

        //настройка размера страницы
        PageRequest pageRequest = PageRequest.of(
                eventAdminParams.getFrom() / eventAdminParams.getSize(), eventAdminParams.getSize());

        //запрашиваем события из базы
        List<Event> events = eventRepository.findAll(conditions, pageRequest).toList();

        //запрашиваем количество одобренных заявок на участие в каждом событии
        Map<Long, Long> eventToRequestsCount = getEventRequests(events);

        //Запрашиваем количество просмотров каждого события
        //       ----------------------------------------------

        List<EventFullDto> eventsFullDto = events.stream().map(eventMapper::toEventFullDto).collect(Collectors.toList());
        for (EventFullDto eventFullDto : eventsFullDto) {
            eventFullDto.setConfirmedRequests(eventToRequestsCount.get(eventFullDto.getId()));
        }

        //Получаем и добавляем просмотры
        Map<Long, Long> views = eventStatisticService.getEventsViews(eventsFullDto.stream()
                .map(EventFullDto::getId)
                .collect(Collectors.toList()));
        for (EventFullDto eventFullDto : eventsFullDto) {
            eventFullDto.setViews(views.get(eventFullDto.getId()));
        }
        //
        return eventsFullDto;
    }

    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEvent) {
        Optional<Event> optEventSaved = eventRepository.findById(eventId);
        Event eventSaved;
        if (optEventSaved.isPresent()) {
            eventSaved = optEventSaved.get();
        } else {
            throw new NotFoundException("Event with ID = " + eventId + " was not found");
        }
        if (updateEvent.getEventDate() != null) {
            updateEventData(updateEvent.getEventDate(), eventSaved);
        }
        if (updateEvent.getStateAction() != null) {
            updateStateOfEventByAdmin(updateEvent.getStateAction(), eventSaved);
        }

        if (updateEvent.getAnnotation() != null) {
            eventSaved.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            Category category = categoryService.getCategoryByIdNotMapping(updateEvent.getCategory());
            eventSaved.setCategory(category);
        }
        if (updateEvent.getDescription() != null) {
            eventSaved.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getLocation() != null) {
            eventSaved.setLat(updateEvent.getLocation().getLat());
            eventSaved.setLon(updateEvent.getLocation().getLon());
        }
        if (updateEvent.getParticipantLimit() != null) {
            eventSaved.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getPaid() != null) {
            eventSaved.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getRequestModeration() != null) {
            eventSaved.setRequestModeration(updateEvent.getRequestModeration());
        }
        if (updateEvent.getTitle() != null) {
            eventSaved.setTitle((updateEvent.getTitle()));
        }

        eventSaved = eventRepository.save(eventSaved);

        //Получаем и добавляем просмотры
        Map<Long, Long> views = eventStatisticService.getEventsViews(List.of(eventSaved).stream()
                .map(Event::getId)
                .collect(Collectors.toList()));

        EventFullDto eventFullDto = eventMapper.toEventFullDto(eventSaved);
        eventFullDto.setViews(views.get(eventFullDto.getId()));

        log.info("Событие ID = {} успешно обновлено от имени администратора", eventId);
        return eventFullDto;
    }


    // Часть public

    public List<EventShortDto> getAllEventsByUser(EventPublicParams request, HttpServletRequest httpServletRequest) {


        //формируем условие выборки
        BooleanExpression conditions = makeEventsQueryConditionsForPublic(request);

        //настройка размера страницы и типа сортировки
        PageRequest pageRequest = PageRequest.of(
                request.getFrom() / request.getSize(), request.getSize());

        //запрашиваем события из базы
        List<Event> events = eventRepository.findAll(conditions, pageRequest).toList();

        //запрашиваем количество одобренных заявок на участие в каждом событии
        Map<Long, Long> eventToRequestsCount = getEventRequests(events);

        List<EventShortDto> eventsShortDto = events.stream().map(eventMapper::toEventShortDto).collect(Collectors.toList());

        //Получаем и добавляем просмотры
        Map<Long, Long> views = eventStatisticService.getEventsViews(eventsShortDto.stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toList()));
        for (EventShortDto eventShortDto : eventsShortDto) {
            eventShortDto.setViews(views.get(eventShortDto.getId()));
        }
        //

        for (EventShortDto eventShortDto : eventsShortDto) {
            eventShortDto.setConfirmedRequests(eventToRequestsCount.get(eventShortDto.getId()));
        }

        if (request.getSort().equals(SortType.VIEWS.toString())) {
            return eventsShortDto.stream().sorted(new EventSortByViews()).collect(Collectors.toList());
        } else {
            return eventsShortDto.stream().sorted(new EventSortByEventDate()).collect(Collectors.toList());
        }
    }

    public EventFullDto getEventDtoById(Long id, HttpServletRequest httpServletRequest) {

        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event must be published"));

        Map<Long, Long> views = eventStatisticService.getEventsViews(List.of(event).stream()
                .map(Event::getId)
                .collect(Collectors.toList()));
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        eventFullDto.setViews(views.get(eventFullDto.getId()));

        return eventFullDto;
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
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isPresent()) {
            return eventOptional.get();
        }
        throw new NotFoundException("Event with ID = " + eventId + " was not found");
    }

    public void addRequestToEvent(Event event) {
        eventRepository.save(event);
    }

    // Собираем условие по которому будем выбирать события из базы данных для публичного запроса
    private static BooleanExpression makeEventsQueryConditionsForPublic(EventPublicParams request) {
        QEvent event = QEvent.event;

        List<BooleanExpression> conditions = new ArrayList<>();

        // поиск фрагмента текста в аннотации, описании и заголовке
        if (request.getText() != null && !request.getText().isBlank()) {
            String textToSearch = request.getText();
            conditions.add(
                    event.title.containsIgnoreCase(textToSearch)
                            .or(event.annotation.containsIgnoreCase(textToSearch))
                            .or(event.description.containsIgnoreCase(textToSearch))
            );
        }

        // фильтрация по списку категорий
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
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

    // Компаратор для сортировки по количеству просмотров
    public static class EventSortByViews implements Comparator<EventShortDto> {

        @Override
        public int compare(EventShortDto o1, EventShortDto o2) {
            return Long.compare(o1.getViews(), o2.getViews());
        }
    }

    // Компаратор для сортировки по дате события
    public static class EventSortByEventDate implements Comparator<EventShortDto> {

        @Override
        public int compare(EventShortDto o1, EventShortDto o2) {
            return o1.getEventDate().compareTo(o2.getEventDate());
        }
    }

    // Собираем условие по которому будем выбирать события из базы данных для запроса администратора
    private static BooleanExpression makeEventsQueryConditionsForAdmin(EventAdminParams request) {
        QEvent event = QEvent.event;

        List<BooleanExpression> conditions = new ArrayList<>();

        // фильтрация по списку пользователь
        if (request.getUsers() !=null && !request.getUsers().isEmpty()) {
            conditions.add(
                    event.initiator.id.in(request.getUsers())
            );
        }

        //    фильтрация событий по статусу
        if (request.getStates() != null && !request.getStates().isEmpty()) {
            List<EventState> states = request.getStates().stream().map(EventState::valueOf).collect(Collectors.toList());
            conditions.add(
                    QEvent.event.state.in(states)
            );
        }

        // фильтрация по списку категорий
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            conditions.add(
                    event.category.id.in(request.getCategories())
            );
        }

//         фильтрация по временному диапазону, если не указано начало, то выборку производим начиная с настоящего
//         времени только в будущее
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

        return conditions
                .stream()
                .reduce(BooleanExpression::and)
                .get();
    }

    public List<Event> getAllEventsByListId(List<Long> eventsId) {
        return eventRepository.findAllById(eventsId);
    }


    public void updateEventData(String dataTime, Event eventSaved) {
        if (LocalDateTime.parse(dataTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .isBefore(LocalDateTime.now().plusHours(1))) {
            throw new InvalidRequestException("The start date of the event to be modified must be no earlier " +
                    "than one hour from the date of publication.");
        }
        eventSaved.setEventDate(LocalDateTime.parse(dataTime,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

}


