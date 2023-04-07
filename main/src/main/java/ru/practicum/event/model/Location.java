package ru.practicum.event.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Location {
    @Column(name = "lat")
    private Float lat; // Широта

    @Column(name = "lon")
    private Float lon; // Долгота
}
