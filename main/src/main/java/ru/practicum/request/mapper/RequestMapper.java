package ru.practicum.request.mapper;

import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;

public class RequestMapper {
    private RequestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static ParticipationRequestDto toRequestDTO(Request request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getCreated(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus().name()
        );
    }

}
