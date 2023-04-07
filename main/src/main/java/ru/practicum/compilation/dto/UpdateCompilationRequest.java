package ru.practicum.compilation.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompilationRequest {
    private List<Long> events;

    private Boolean pinned; // Закреплена ли подборка на главной странице сайта

    private String title;
}
