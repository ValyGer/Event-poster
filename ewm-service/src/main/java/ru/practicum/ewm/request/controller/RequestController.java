package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> createRequest(@PathVariable long userId,
                                                                 @NotNull @RequestParam long eventId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(requestService.createRequest(userId, eventId));
    }

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getAllRequestByUser(@PathVariable long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(requestService.getAllRequestByUser(userId));
    }

    @PatchMapping(value = "/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancellationRequest(@PathVariable long userId,
                                                                 @PathVariable long requestId) {
        return ResponseEntity.status(HttpStatus.OK).body(requestService.cancellationRequest(userId, requestId));
    }
}
