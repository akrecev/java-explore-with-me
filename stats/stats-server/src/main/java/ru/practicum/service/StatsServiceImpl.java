package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static ru.practicum.mapper.EndpointHitMapper.toEndpointHit;
import static ru.practicum.mapper.EndpointHitMapper.toEndpointHitDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public EndpointHitDto create(EndpointHitDto hitDto) {
        EndpointHit savedHit = statsRepository.save(toEndpointHit(hitDto));

        return toEndpointHitDto(savedHit);
    }

    @Override
    public List<ViewStatsDto> get(String start, String end, List<String> uris, boolean unique) {
        List<ViewStatsDto> result;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);

        if (Objects.isNull(uris) || uris.isEmpty()) {
            {
                if (unique) {
                    result = statsRepository.getStatsUnique(startTime, endTime);
                } else {
                    result = statsRepository.getStats(startTime, endTime);
                }
            }
        } else {
            if (unique) {
                result = statsRepository.getStatsUniqueWithUris(startTime, endTime, uris);
            } else {
                result = statsRepository.getStatsWithUris(startTime, endTime, uris);
            }
        }

        return result;
    }
}
