package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.ParametersForRequestAdmin;
import ru.practicum.ewm.event.model.ParametersForRequestPublic;
import ru.practicum.ewm.event.service.EventService;

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
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(userId, newEvenDto));
    }

    @GetMapping(value = "/users/{userId}/events")
    public ResponseEntity<List<EventShortDto>> getAllEventForUser(
            @PathVariable Long userId,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {

        List<EventShortDto> listEvents = eventService.getAllEventOfUser(userId, from, size);

        return ResponseEntity.status(HttpStatus.OK).body(listEvents);
    }

    @GetMapping(value = "/users/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> getEventForUserById(@PathVariable Long userId, @PathVariable Long eventId) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getEventOfUserById(userId, eventId));
    }

    @PatchMapping(value = "/users/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> updateEventByUser(
            @PathVariable Long userId, @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.updateEventByUser(userId, eventId,
                updateEventUserRequest));
    }

    // Часть admin

    @GetMapping("/admin/events")
    public ResponseEntity<List<EventFullDto>> getAllEventsByAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        ParametersForRequestAdmin parametersForRequestAdmin = ParametersForRequestAdmin.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllEventsByAdmin(parametersForRequestAdmin));
    }

    @PatchMapping(value = "/admin/events/{eventId}")
    public ResponseEntity<EventFullDto> updateEventByAdmin(
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.updateEventByAdmin(eventId,
                updateEventAdminRequest));
    }

    // Часть public

    @GetMapping("/events")
    public ResponseEntity<List<EventShortDto>> getAllEvents(
                    @RequestParam(required = false) String text,
                    @RequestParam(required = false) List<Long> categories,
                    @RequestParam(required = false) Boolean paid,
                    @RequestParam(defaultValue = "0") long lat,
                    @RequestParam(defaultValue = "0") long lon,
                    @RequestParam(required = false) String rangeStart,
                    @RequestParam(required = false) String rangeEnd,
                    @RequestParam(defaultValue = "false") boolean onlyAvailable,
                    @RequestParam(defaultValue = "EVENT_DATE") String sort,
                    @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                    @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        ParametersForRequestPublic parametersForRequestPublic = ParametersForRequestPublic.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .from(from)
                .size(size)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllEventsByUser(parametersForRequestPublic));
    }
}
