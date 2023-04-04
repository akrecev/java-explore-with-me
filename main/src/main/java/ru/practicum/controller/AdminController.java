package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.servise.CategoryService;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin")
@RequiredArgsConstructor
@Validated
public class AdminController {
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;

    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("Create user {}", newUserRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.create(newUserRequest));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers(
            @RequestParam(value = "ids") List<Long> ids,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        log.info("Get users {}", ids);

        return ResponseEntity.ok(userService.getUsers(ids, from, size));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@Positive @PathVariable Long userId) {
        userService.deleteById(userId);
        log.info("Delete user id={}", userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Create category {}", newCategoryDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.create(newCategoryDto));
    }

    @PatchMapping("/categories/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(
            @Positive @PathVariable Long catId,
            @Valid @RequestBody CategoryDto categoryDto
    ) {
        log.info("Update category {}", categoryDto);
        return ResponseEntity.ok(categoryService.update(catId, categoryDto));
    }

    @DeleteMapping("/categories/{catId}")
    public ResponseEntity<Void> deleteCategory(@Positive @PathVariable Long catId) {
        categoryService.deleteById(catId);
        log.info("Delete category id={}", catId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventFullDto>> getEventsByAdmin(
            @RequestParam(value = "users") List<Long> users,
            @RequestParam(value = "states", required = false) List<String> states,
            @RequestParam(value = "categories") List<Long> categories,
            @RequestParam(value = "rangeStart", required = false) String rangeStart,
            @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        log.info("Get events by admin request");

        return ResponseEntity.ok(eventService.getEventsByAdmin(
                users, states, categories, rangeStart, rangeEnd, from, size
        ));
    }

    @PatchMapping("/events/{eventId}")
    public ResponseEntity<EventFullDto> updateEventByAdmin(
            @Positive @PathVariable Long eventId,
            @RequestBody UpdateEventAdminRequest updateEvent
    ) {
        log.info("Update event id={} by admin", eventId);

        return ResponseEntity.ok(eventService.updateEventByAdmin(eventId, updateEvent));
    }

    @PostMapping("/compilations")
    public ResponseEntity<CompilationDto> createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Create compilation {}", newCompilationDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(compilationService.create(newCompilationDto));
    }

    @PatchMapping("/compilations/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(
            @Positive @PathVariable Long compId,
            @Valid @RequestBody UpdateCompilationRequest compilationRequest
    ) {
        log.info("Update compilation {}", compilationRequest);
        return ResponseEntity.ok(compilationService.update(compId, compilationRequest));
    }

    @DeleteMapping("/compilations/{compId}")
    public ResponseEntity<Void> deleteCompilationById(@Positive @PathVariable Long compId) {
        compilationService.deleteById(compId);
        log.info("Delete compilation id={}", compId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
