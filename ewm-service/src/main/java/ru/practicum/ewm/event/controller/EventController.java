package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
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
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllEventOfUser(userId, from, size));
    }

    @GetMapping(value = "/users/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> getEventForUserById(@PathVariable Long userId, @PathVariable Long eventId) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getEventOfUserById(userId, eventId));
    }

    @PatchMapping(value = "/users/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> updateEventByUser(
            @PathVariable Long userId, @PathVariable Long eventId,
            @Valid @RequestBody NewEventDtoForUpdate newEventDtoForUpdate) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.updateEvent(userId, eventId,
                newEventDtoForUpdate));
    }

    // Часть admin

    @GetMapping("/admin/events")
    public List<EventFullDto> getAllEventsByAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        ParametersForRequest parametersForRequest = new ParametersForRequest(
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size);
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllEventsByAdmin(parametersForRequest));
    }



    // Часть public

    @GetMapping("/events")
    public List<EventShortDto> getAllEvents
}
