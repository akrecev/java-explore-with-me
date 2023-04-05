package ru.practicum.event.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private Long id;

    private Long views; // Количество просмотрев события


}
