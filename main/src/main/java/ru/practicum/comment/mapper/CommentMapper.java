package ru.practicum.comment.mapper;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentPublicDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;
import ru.practicum.utility.Formatter;

import java.time.LocalDateTime;

public class CommentMapper {
    private CommentMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getDescription(),
                comment.getCreated().format(Formatter.TIME_FORMATTER),
                comment.getEvent().getId(),
                comment.getCommentator().getId()
        );
    }

    public static CommentPublicDto toCommentPublicDto(Comment comment) {
        return new CommentPublicDto(
                comment.getId(),
                comment.getDescription(),
                comment.getCreated().format(Formatter.TIME_FORMATTER),
                comment.getEvent().getTitle(),
                comment.getCommentator().getName()
        );
    }

    public static Comment toComment(CommentDto commentDto, User commentator, Event event) {
        return new Comment(
                commentDto.getId(),
                commentDto.getDescription(),
                LocalDateTime.now(),
                event,
                commentator
        );
    }

}
