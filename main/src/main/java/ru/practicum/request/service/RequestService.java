package ru.practicum.request.service;

import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto create(Long userId, Long eventId);

    List<ParticipationRequestDto> getAllRequestsByUser(Long userId);

    List<ParticipationRequestDto> getRequestsByEvent(Long userId, Long eventId);

    ParticipationRequestDto canselRequestByUser(Long userId, Long requestId);

    EventRequestStatusUpdateResult updateRequests(
            Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequests
    );
}
