package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventShortDto extends EventDto {

    private String annotation;

    private CategoryDto category;

    private String title;

    private UserShortDto initiator;

    private Long confirmedRequests; // Количество одобренных заявок на участие в данном событии

    private String eventDate; // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    private Boolean paid; // Нужно ли оплачивать участие

}
