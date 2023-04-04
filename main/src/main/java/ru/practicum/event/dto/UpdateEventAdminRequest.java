package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.event.model.Location;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 3, max = 120)
    private String title; // Заголовок события

    @Size(min = 20, max = 7000)
    private String description;

    private String eventDate; // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    private Location location;

    private Boolean paid; // Нужно ли оплачивать участие

    private Long participantLimit; // Ограничение на количество участников. Default 0 - отсутствие ограничения

    private Boolean requestModeration; // Нужна ли пре-модерация заявок на участие

    private String stateAction; // Новое состояние события Enum: [ PUBLISH_EVENT, REJECT_EVENT ]

}
