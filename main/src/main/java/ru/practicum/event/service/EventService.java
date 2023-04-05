package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto create(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getAllEventsByUser(Long userId, Integer from, Integer size);

    EventFullDto getEventByUser(Long userId, Long eventId);

    List<EventFullDto> getEventsByAdmin(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size
    );

    List<EventShortDto> getPublicEvents(
            String text, List<Long> categories, Boolean paid, String rangeStart,
            String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest request
    );

    EventFullDto getById(Long id, HttpServletRequest request);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventRequest updateEvent);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventRequest updateEvent);
}
