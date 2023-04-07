package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.event.model.Location;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewEventDto {
    @NotNull
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private Long category;

    @NotNull
    @Size(min = 3, max = 120)
    private String title; // Заголовок события

    @NotNull
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    private String eventDate; // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    @NotNull
    private Location location;

    private Boolean paid; // Нужно ли оплачивать участие

    private Long participantLimit; // Ограничение на количество участников. Default 0 - отсутствие ограничения

    private Boolean requestModeration; // Нужна ли пре-модерация заявок на участие

}
