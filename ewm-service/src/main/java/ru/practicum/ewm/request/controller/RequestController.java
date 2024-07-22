package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping(value = "/users/{userId}/requests")
    public ResponseEntity<ParticipationRequestDto> createRequest(@PathVariable Long userId,
                                                                 @NotNull @RequestParam Long eventId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(requestService.createRequest(userId, eventId));
    }

    @GetMapping("/users/{userId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getAllRequestByUser(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(requestService.getAllRequestByUser(userId));
    }

    @PatchMapping(value = "/users/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancellationRequest(@PathVariable Long userId,
                                                                       @PathVariable Long requestId) {
        return ResponseEntity.status(HttpStatus.OK).body(requestService.cancellationRequest(userId, requestId));
    }
}
