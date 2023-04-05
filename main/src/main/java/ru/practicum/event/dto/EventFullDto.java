package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserShortDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFullDto extends EventDto {

    private String annotation;

    private CategoryDto category;

    private String title;

    private String state; // Список состояний жизненного цикла события Enum: [ PENDING, PUBLISHED, CANCELED ]

    private String description;

    private UserShortDto initiator;

    private Long confirmedRequests; // Количество одобренных заявок на участие в данном событии

    private String createdOn; // Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")

    private String eventDate; // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    private String publishedOn; // Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")

    private Location location;

    private Boolean paid; // Нужно ли оплачивать участие

    private Long participantLimit; // Ограничение на количество участников. Default 0 - отсутствие ограничения

    private Boolean requestModeration; // Нужна ли пре-модерация заявок на участие

}
