package ru.practicum.event.model;

import lombok.*;
import ru.practicum.category.model.Category;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 120)
    private String title;

    @Enumerated(EnumType.STRING)
    private State state; // Список состояний жизненного цикла события Enum: [ PENDING, PUBLISHED, CANCELED ]

    @Column(nullable = false, length = 7000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Column(name = "created_on")
    private LocalDateTime createdOn; // Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate; // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    @Column(name = "published_on")
    private LocalDateTime publishedOn; // Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")

    @Embedded
    private Location location;

    @Column(name = "is_paid")
    private Boolean paid; // Нужно ли оплачивать участие

    @Column(name = "participant_limit")
    private Long participantLimit; // Ограничение на количество участников. Default 0 - отсутствие ограничения

    @Column(name = "is_moderation")
    private Boolean requestModeration; // Нужна ли пре-модерация заявок на участие

    private Long views; // Количество просмотрев события

}
