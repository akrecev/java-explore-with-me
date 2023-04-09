package ru.practicum.comment.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;

    @NotNull
    @Size(min = 2, max = 7000)
    private String description;

    private String created;

    private Long eventId;

    private Long commentatorId;

}
