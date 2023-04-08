package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.utility.Formatter;
import ru.practicum.utility.MyPageRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.event.mapper.EventMapper.toEvent;
import static ru.practicum.event.mapper.EventMapper.toEventFullDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;


    @Override
    @Transactional
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        LocalDateTime eventDate = LocalDateTime.parse(newEventDto.getEventDate(), Formatter.TIME_FORMATTER);

        if (eventDate.isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new ConflictException(
                    "Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value:" + eventDate
            );
        }

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        Event event = toEvent(newEventDto);
        event.setCategory(categoryRepository
                .findById(newEventDto.getCategory())
                .orElseThrow(() -> new DataNotFoundException(
                        "Category with id=" + newEventDto.getCategory() + " was not found"
                )));
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(initiator);
        event.setState(State.PENDING);
        event.setViews(0L);

        Event savedEvent = eventRepository.save(event);

        return mappingToEventFullDto(savedEvent);
    }

    @Override
    public List<EventShortDto> getAllEventsByUser(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));

        return eventRepository
                .findEventsByInitiatorId(userId, new MyPageRequest(from, size))
                .stream()
                .map(EventMapper::toEventShortDto)
                .peek(s -> s.setConfirmedRequests(countConfirmedRequests(s.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));

        return mappingToEventFullDto(event);
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                               String rangeStart, String rangeEnd, Integer from, Integer size) {
        List<User> usersList = userRepository.findAllById(users);
        List<State> statesList = new ArrayList<>();

        if (Objects.nonNull(states)) {
            for (String stateName : states) {
                State state = State.valueOf(stateName);
                statesList.add(state);
            }
        } else {
            statesList = Arrays.asList(State.values());
        }

        List<Category> categoriesList = prepareCategories(categories);
        LocalDateTime start = prepareLocalDateTime(rangeStart, LocalDateTime.now());
        LocalDateTime end = prepareLocalDateTime(rangeEnd, LocalDateTime.now().plusYears(100L));

        Page<Event> events = eventRepository.findEventsByInitiatorInAndStateInAndCategoryInAndEventDateBetween(
                usersList, statesList, categoriesList, start, end, MyPageRequest.of(from, size)
        );

        return events.stream()
                .map(EventMapper::toEventFullDto)
                .peek((s) -> s.setConfirmedRequests(countConfirmedRequests(s.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getPublicEvents(
            String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd,
            Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest request
    ) {

        List<Category> categoriesList = prepareCategories(categories);
        LocalDateTime start = prepareLocalDateTime(rangeStart, LocalDateTime.now());
        LocalDateTime end = prepareLocalDateTime(rangeEnd, LocalDateTime.now().plusYears(100L));

        State state = State.PUBLISHED;

        List<Event> events = eventRepository.search(state, text, categoriesList, paid, start, end,
                MyPageRequest.of(from, size)).toList();

        if (onlyAvailable) {
            events = events.stream()
                    .filter(e -> e.getParticipantLimit() >= countConfirmedRequests(e.getId()))
                    .collect(Collectors.toList());
        }

        List<EventShortDto> eventsList = events.stream()
                .map(EventMapper::toEventShortDto)
                .peek(s -> s.setConfirmedRequests(countConfirmedRequests(s.getId())))
                .collect(Collectors.toList());

        EndpointHitDto hitDto = EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        statsClient.hit(hitDto);

        for (EventShortDto eventShortDto : eventsList) {
            setViews(eventShortDto);
        }

        if (sort.equals("VIEWS")) {
            eventsList.sort(Comparator.comparing(EventShortDto::getViews));
        }

        return eventsList;
    }

    @Override
    public EventFullDto getById(Long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Event with id=" + id + " was not found"));

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Event must be published.");
        }

        EndpointHitDto hitDto = EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        statsClient.hit(hitDto);
        EventFullDto eventFullDto = toEventFullDto(event);
        setViews(eventFullDto);
        eventFullDto.setConfirmedRequests(countConfirmedRequests(id));

        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventRequest updateRequest) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getState().equals(State.PENDING) && !event.getState().equals(State.CANCELED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        updateEvent(updateRequest, event);

        if (!Objects.isNull(updateRequest.getStateAction())) {
            if (updateRequest.getStateAction().equals(StateAction.SEND_TO_REVIEW.toString())) {
                event.setState(State.PENDING);
            }
            if (updateRequest.getStateAction().equals(StateAction.CANCEL_REVIEW.toString())) {
                event.setState(State.CANCELED);
            }
        }

        Event updatedEvent = eventRepository.save(event);

        return mappingToEventFullDto(updatedEvent);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventRequest updateRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));

        updateEvent(updateRequest, event);

        if (!Objects.isNull(updateRequest.getStateAction())) {
            if (updateRequest.getStateAction().equals(StateAction.PUBLISH_EVENT.toString())) {
                if (event.getState().equals(State.PENDING)) {
                    event.setState(State.PUBLISHED);
                } else {
                    throw new ConflictException(
                            "Cannot publish the event because it's not in the right state: "
                                    + event.getState()
                    );
                }
            }
            if (updateRequest.getStateAction().equals(StateAction.REJECT_EVENT.toString())) {
                if (!event.getState().equals(State.PUBLISHED)) {
                    event.setState(State.CANCELED);
                } else {
                    throw new ConflictException(
                            "Cannot cancel the event because it's not in the right state: PUBLISHED"
                    );
                }
            }
        }
        Event updatedEvent = eventRepository.save(event);

        return mappingToEventFullDto(updatedEvent);
    }

    private static LocalDateTime prepareLocalDateTime(String rangeStart, LocalDateTime now) {
        LocalDateTime start;

        if (Objects.isNull(rangeStart)) {
            start = now;
        } else {
            start = LocalDateTime.parse(rangeStart, Formatter.TIME_FORMATTER);
        }
        return start;
    }

    private List<Category> prepareCategories(List<Long> categories) {
        List<Category> categoriesList;
        if (Objects.nonNull(categories)) {
            categoriesList = categoryRepository.findAllById(categories);
        } else {
            categoriesList = categoryRepository.findAll();
        }
        return categoriesList;
    }

    private Long countConfirmedRequests(Long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    private EventFullDto mappingToEventFullDto(Event event) {
        EventFullDto eventFullDto = toEventFullDto(event);
        eventFullDto.setConfirmedRequests(countConfirmedRequests(event.getId()));
        return eventFullDto;
    }

    private <T extends EventDto> void setViews(T eventDto) {
        Long views = 0L;
        List<ViewStatsDto> viewStatsDtoList = statsClient.stats(
                LocalDateTime.now().minusYears(100L).format(Formatter.TIME_FORMATTER),
                LocalDateTime.now().plusYears(100L).format(Formatter.TIME_FORMATTER),
                "/events/" + eventDto.getId(),
                false
        );

        if (viewStatsDtoList.size() != 0) {
            views = viewStatsDtoList.get(0).getHits();
        }
        eventDto.setViews(views);
    }

    private void updateEvent(UpdateEventRequest updateRequest, Event event) {

        if (updateRequest.getAnnotation() != null && !updateRequest.getAnnotation().equals(event.getAnnotation())) {
            event.setAnnotation(updateRequest.getAnnotation());
        }

        if (updateRequest.getCategory() != null
                && !updateRequest.getCategory().equals(event.getCategory().getId())) {
            event.setCategory(categoryRepository
                    .findById(updateRequest.getCategory()).orElseThrow(
                            () -> new DataNotFoundException(
                                    "Category with id=" + updateRequest.getCategory() + " was not found"
                            )));
        }

        if (updateRequest.getTitle() != null && !updateRequest.getTitle().equals(event.getTitle())) {
            event.setTitle(updateRequest.getTitle());
        }

        if (updateRequest.getDescription() != null && !updateRequest.getDescription().equals(event.getDescription())) {
            event.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.getEventDate() != null) {
            LocalDateTime eventDate = LocalDateTime.parse(updateRequest.getEventDate(), Formatter.TIME_FORMATTER);

            if (!eventDate.equals(event.getEventDate())) {
                if (eventDate.isBefore(LocalDateTime.now().plusHours(2L))) {
                    throw new ConflictException(
                            "Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value:"
                                    + eventDate
                    );
                }
                event.setEventDate(eventDate);
            }
        }

        if (updateRequest.getLocation() != null && !updateRequest.getLocation().equals(event.getLocation())) {
            event.setLocation(updateRequest.getLocation());
        }

        if (updateRequest.getPaid() != null && !updateRequest.getPaid().equals(event.getPaid())) {
            event.setPaid(updateRequest.getPaid());
        }

        if (updateRequest.getParticipantLimit() != null
                && !updateRequest.getParticipantLimit().equals(event.getParticipantLimit())) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }

        if (updateRequest.getRequestModeration() != null
                && !updateRequest.getRequestModeration().equals(event.getRequestModeration())) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
    }

}
