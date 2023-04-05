package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.servise.CategoryService;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class PublicController {
    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories(
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        log.info("Get categories");

        return ResponseEntity.ok(categoryService.getAll(from, size));
    }

    @GetMapping("/categories/{catId}")
    public ResponseEntity<CategoryDto> getCategoryById(@Positive @PathVariable Long catId) {
        log.info("Get category id={}", catId);

        return ResponseEntity.ok(categoryService.getById(catId));
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventShortDto>> getPublicEvents(
            @RequestParam(value = "text", defaultValue = "") String text,
            @RequestParam(value = "categories", required = false) List<Long> categories,
            @RequestParam(value = "paid", required = false) Boolean paid,
            @RequestParam(value = "rangeStart", required = false) String rangeStart,
            @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(value = "sort", defaultValue = "EVENT_DATE") String sort,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size,
            HttpServletRequest request
    ) {
        log.info("Get events ");

        return ResponseEntity.ok(eventService.getPublicEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request
        ));
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<EventFullDto> getEventById(@Positive @PathVariable Long id, HttpServletRequest request) {
        log.info("Get event id={}", id);

        return ResponseEntity.ok(eventService.getById(id, request));
    }

    @GetMapping("/compilations")
    public ResponseEntity<List<CompilationDto>> getAllCompilations(
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        log.info("Get compilations");

        return ResponseEntity.ok(compilationService.getAll(from, size));
    }

    @GetMapping("/compilations/{compId}")
    public ResponseEntity<CompilationDto> getCompilationById(@Positive @PathVariable Long compId) {
        log.info("Get compilation id={}", compId);

        return ResponseEntity.ok(compilationService.getById(compId));
    }

}
