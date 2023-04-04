package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventInitiatorIdAndEventId(Long initiatorId, Long eventId);

    List<Request> findAllByEventInitiatorIdAndEventIdAndIdIn(Long initiatorId, Long eventId, List<Long> requestIdList);

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);
}
