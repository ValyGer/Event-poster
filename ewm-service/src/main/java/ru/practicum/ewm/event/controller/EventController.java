package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // Часть private

    @PostMapping(value = "/users/{userId}/events")
    public ResponseEntity<EventFullDto> createEvent(@PathVariable Long userId,
                                                    @Valid @RequestBody NewEvenDto newEvenDto) {
        System.out.println("привет Post /users/{userId}/events");
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(userId, newEvenDto));
    }

    @GetMapping("/users/{userId}/events")
    public ResponseEntity<List<EventShortDto>> getAllEventForUser(
            @PathVariable Long userId,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        List<EventShortDto> listEvents = eventService.getAllEventOfUser(userId, from, size);
        System.out.println("привет Get /users/{userId}/events");
        return ResponseEntity.status(HttpStatus.OK).body(listEvents);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> getEventForUserById(@PathVariable Long userId, @PathVariable Long eventId) {
        System.out.println("привет Get /users/{userId}/events/{eventId}");
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getEventOfUserById(userId, eventId));
    }

    @PatchMapping(value = "/users/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> updateEventByUser(
            @PathVariable Long userId, @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        System.out.println("привет Patch /users/{userId}/events/{eventId}");

        return ResponseEntity.status(HttpStatus.OK).body(eventService.updateEventByUser(userId, eventId,
                updateEventUserRequest));
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequestEventByUser(@PathVariable Long userId,
                                                                               @PathVariable Long eventId) {
        System.out.println("привет get /users/{userId}/events/{eventId}/requests");
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getRequestEventByUser(userId, eventId));
    }

    @PatchMapping(value = "/users/{userId}/events/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> changeRequestEventStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest request) {
        System.out.println("привет patch /users/{userId}/events/{eventId}/requests");

        return ResponseEntity.status(HttpStatus.OK)
                .body(eventService.changeRequestEventStatus(userId, eventId, request));
    }


    // Часть admin

    @GetMapping("/admin/events")
    public ResponseEntity<List<EventFullDto>> getAllEventsByAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        EventAdminParams eventAdminParams = EventAdminParams.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        System.out.println("привет get /admin/events");
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllEventsByAdmin(eventAdminParams));
    }

    @PatchMapping(value = "/admin/events/{eventId}")
    public ResponseEntity<EventFullDto> updateEventByAdmin(
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {

        System.out.println("привет patch /admin/events/{eventId}");

        return ResponseEntity.status(HttpStatus.OK).body(eventService.updateEventByAdmin(eventId,
                updateEventAdminRequest));
    }

    // Часть public

    @GetMapping("/events")
    public ResponseEntity<List<EventShortDto>> getAllEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") boolean onlyAvailable,
            @RequestParam(defaultValue = "EVENT_DATE") String sort,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
            @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        EventPublicParams eventPublicParams = EventPublicParams.builder()
                .state(EventState.PUBLISHED)
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .from(from)
                .size(size)
                .sort(sort)
                .build();
        System.out.println("привет get /events");

        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllEventsByUser(eventPublicParams));
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<EventFullDto> getEventDtoById(@PathVariable Long id) {
        System.out.println("привет get /events/{id}");

        return ResponseEntity.status(HttpStatus.OK).body(eventService.getEventDtoById(id));
    }
}
