package ru.practicum.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCompilationDto {
    private List<Long> events;

    private Boolean pinned; // Закреплена ли подборка на главной странице сайта

    @NotBlank
    private String title;
}
