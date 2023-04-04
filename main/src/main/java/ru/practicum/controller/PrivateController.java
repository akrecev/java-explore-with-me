package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class PrivateController {
    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping("/{userId}/events")
    public ResponseEntity<EventFullDto> createEvent(
            @Positive @PathVariable Long userId,
            @Valid @RequestBody NewEventDto newEventDto
    ) {
        log.info("Create event {}", newEventDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventService.create(userId, newEventDto));
    }

    @GetMapping("/{userId}/events")
    public ResponseEntity<List<EventShortDto>> getAllEventsByUser(
            @Positive @PathVariable Long userId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        log.info("Get events of user id={}", userId);

        return ResponseEntity.ok(eventService.getAllEventsByUser(userId, from, size));
    }

    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> getEventByUser(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long eventId
    ) {
        log.info("Get event id={} of user id={}", eventId, userId);

        return ResponseEntity.ok(eventService.getEventByUser(userId, eventId));
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> updateEventByUser(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateEvent
    ) {
        log.info("Update event id={} by user id={}", eventId, userId);

        return ResponseEntity.ok(eventService.updateEventByUser(userId, eventId, updateEvent));
    }

    @PostMapping("/{userId}/requests")
    public ResponseEntity<ParticipationRequestDto> createRequest(
            @Positive @PathVariable Long userId,
            @Positive @RequestParam(value = "eventId") Long eventId
    ) {
        log.info("Create request user id={} for event id={}", userId, eventId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(requestService.create(userId, eventId));
    }

    @GetMapping("/{userId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getAllRequestsByUser(
            @Positive @PathVariable Long userId
    ) {
        log.info("Get requests of user id={}", userId);

        return ResponseEntity.ok(requestService.getAllRequestsByUser(userId));
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsByEvent(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long eventId
    ) {
        log.info("Get requests user id={} of event id={}", userId, eventId);

        return ResponseEntity.ok(requestService.getRequestsByEvent(userId, eventId));
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> canselRequestByUser(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long requestId
    ) {
        log.info("Cansel request id={} by user id={}", requestId, userId);

        return ResponseEntity.ok(requestService.canselRequestByUser(userId, requestId));
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequests(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest updateRequests
    ) {
        log.info("Update requests {}", updateRequests);

        return ResponseEntity.ok(requestService.updateRequests(userId, eventId, updateRequests));
    }

}
