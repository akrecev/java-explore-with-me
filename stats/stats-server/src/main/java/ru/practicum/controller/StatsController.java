package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public EndpointHitDto crate(@RequestBody @Valid EndpointHitDto hitDto) {
        return statsService.create(hitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> get(@RequestParam String start,
                                  @RequestParam String end,
                                  @RequestParam List<String> uris,
                                  @RequestParam(defaultValue = "false") Boolean unique) {
        return statsService.get(start, end, uris, unique);
    }
}
