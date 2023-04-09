package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventRequest;
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
    private final CommentService commentService;

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
        log.info("Get events by user id={}", userId);

        return ResponseEntity.ok(eventService.getAllEventsByUser(userId, from, size));
    }

    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> getEventByUser(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long eventId
    ) {
        log.info("Get event id={} by user id={}", eventId, userId);

        return ResponseEntity.ok(eventService.getEventByUser(userId, eventId));
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> updateEventByUser(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventRequest updateEvent
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
        log.info("Get requests by user id={}", userId);

        return ResponseEntity.ok(requestService.getAllRequestsByUser(userId));
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsByEvent(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long eventId
    ) {
        log.info("Get requests user id={} by event id={}", userId, eventId);

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

    @PostMapping("/{userId}/comments/{eventId}")
    public ResponseEntity<CommentDto> createComment(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long eventId,
            @Valid @RequestBody CommentDto newComment
    ) {
        log.info("Create comment {}", newComment);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commentService.create(userId, eventId, newComment));
    }

    @GetMapping("/{userId}/comments")
    public ResponseEntity<List<CommentDto>> getAllCommentsByUser(
            @Positive @PathVariable Long userId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        log.info("Get comments by user id={}", userId);

        return ResponseEntity.ok(commentService.getAllByUser(userId, from, size));
    }

    @GetMapping("/{userId}/comments/{commentId}")
    public ResponseEntity<CommentDto> getCommentById(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long commentId
    ) {
        log.info("Get comment id={} by user id={}", commentId, userId);

        return ResponseEntity.ok(commentService.getById(userId, commentId));
    }

    @PatchMapping("/{userId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateCommentByUser(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long commentId,
            @RequestBody CommentDto updateComment
    ) {
        log.info("Update comment id={} by user id={}", commentId, userId);

        return ResponseEntity.ok(commentService.update(userId, commentId, updateComment));
    }

    @DeleteMapping("/{userId}/comments/{commentId}")
    public ResponseEntity<Void> deleteCommentByUser(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long commentId
    ) {
        commentService.deleteByUser(userId, commentId);
        log.info("Delete comment id={} by user id={}", commentId, userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
