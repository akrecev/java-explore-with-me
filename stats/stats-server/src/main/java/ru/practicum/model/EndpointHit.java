package ru.practicum.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class EndpointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String app;
    @Column(nullable = false, length = 2048)
    private String uri;
    @Column(nullable = false, length = 20)
    private String ip;
    @Column(nullable = false)
    private LocalDateTime timestamp;

}
