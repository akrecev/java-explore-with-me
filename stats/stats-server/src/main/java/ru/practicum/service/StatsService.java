package ru.practicum.service;

import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

import java.util.List;

public interface StatsService {
    EndpointHitDto create(EndpointHitDto hitDto);

    List<ViewStatsDto> get(String start, String end, List<String> uris, boolean unique);
}
