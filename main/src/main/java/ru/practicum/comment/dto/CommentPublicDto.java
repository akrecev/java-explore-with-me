package ru.practicum.comment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentPublicDto {
    private Long id;

    private String description;

    private String created;

    private String eventTitle;

    private String commentatorName;
}
