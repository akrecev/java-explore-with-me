package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.request.mapper.RequestMapper.toRequestDTO;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;


    @Override
    @Transactional
    public ParticipationRequestDto create(Long userId, Long eventId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User", userId));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Event", eventId));

        if (userId.equals(event.getInitiator().getId())) {
            throw new ConflictException("User id=" + userId + " is initiator by event id=" + eventId);
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Event id=" + eventId + "does not published.");
        }

        if (requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)
                >= event.getParticipantLimit()) {
            throw new ConflictException("The count of confirmed requests is empty for event id=" + eventId);
        }

        Request request = Request.builder()
                .event(event)
                .requester(requester)
                .build();

        if (event.getRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        request.setCreated(LocalDateTime.now().withNano(0));

        requestRepository.save(request);

        return toRequestDTO(request);
    }

    @Override
    public List<ParticipationRequestDto> getAllRequestsByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User", userId));

        List<Request> requestList = requestRepository.findAllByRequesterId(userId);

        return requestList.stream()
                .map(RequestMapper::toRequestDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByEvent(Long userId, Long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User", userId));

        eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Event", eventId));

        List<Request> requestList = requestRepository.findAllByEventInitiatorIdAndEventId(userId, eventId);

        return requestList.stream()
                .map(RequestMapper::toRequestDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto canselRequestByUser(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User", userId));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new DataNotFoundException("Request", requestId));
        request.setStatus(RequestStatus.CANCELED);

        Request rejectedRequest = requestRepository.save(request);

        return toRequestDTO(rejectedRequest);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequests(
            Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequests
    ) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User", userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Event", eventId));

        if (requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)
                >= event.getParticipantLimit()) {
            throw new ConflictException("The count of confirmed requests is empty for event id=" + eventId);
        }

        List<Long> requestIdList = updateRequests.getRequestIds();
        List<Request> requestList = requestRepository
                .findAllByEventInitiatorIdAndEventIdAndIdIn(userId, eventId, requestIdList);
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        switch (updateRequests.getStatus()) {
            case "CONFIRMED":
                for (Request request : requestList) {
                    if (!request.getStatus().equals(RequestStatus.PENDING)) {
                        throw new ConflictException("Request status is not PENDING");
                    }
                    request.setStatus(RequestStatus.CONFIRMED);
                    requestRepository.save(request);
                    result.getConfirmedRequests().add(toRequestDTO(request));
                }
                break;
            case "REJECTED":
                for (Request request : requestList) {
                    if (!request.getStatus().equals(RequestStatus.PENDING)) {
                        throw new ConflictException("Request status is not PENDING");
                    }
                    request.setStatus(RequestStatus.REJECTED);
                    requestRepository.saveAndFlush(request);
                    result.getRejectedRequests().add(toRequestDTO(request));
                }
                break;
        }
        return result;
    }
}
