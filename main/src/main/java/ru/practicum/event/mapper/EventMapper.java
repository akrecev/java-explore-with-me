package ru.practicum.event.mapper;

import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.utility.Formatter;

import java.time.LocalDateTime;

public class EventMapper {
    private EventMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static EventFullDto toEventFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto(
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getTitle(),
                event.getState().name(),
                event.getDescription(),
                UserMapper.toUserShortDto(event.getInitiator()),
                0L,
                event.getCreatedOn().format(Formatter.TIME_FORMATTER),
                event.getEventDate().format(Formatter.TIME_FORMATTER),
                LocalDateTime.now().format(Formatter.TIME_FORMATTER),
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getRequestModeration()
        );
        eventFullDto.setId(event.getId());

        return eventFullDto;
    }

    public static EventShortDto toEventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto(
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getTitle(),
                UserMapper.toUserShortDto(event.getInitiator()),
                0L,
                event.getEventDate().format(Formatter.TIME_FORMATTER),
                event.getPaid()
        );
        eventShortDto.setId(event.getId());

        return eventShortDto;
    }

    public static Event toEvent(NewEventDto newEventDto) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .title(newEventDto.getTitle())
                .description(newEventDto.getDescription())
                .eventDate(LocalDateTime.parse(newEventDto.getEventDate(), Formatter.TIME_FORMATTER))
                .location(newEventDto.getLocation())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .build();
    }

}
