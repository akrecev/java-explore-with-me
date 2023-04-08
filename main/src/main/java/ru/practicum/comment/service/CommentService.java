package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentPublicDto;

import java.util.List;

public interface CommentService {
    CommentDto create(Long userId, Long eventId, CommentDto newComment);

    List<CommentDto> getAllCommentsByUser(Long userId, Integer from, Integer size);

    List<CommentDto> getAllCommentsByEvent(Long eventId, Integer from, Integer size);

    List<CommentPublicDto> getAllCommentsByEventPublic(Long eventId, Integer from, Integer size);

    CommentDto getCommentById(Long userId, Long commentId);

    CommentDto updateCommentByUser(Long userId, Long commentId, CommentDto updateComment);

    void deleteCommentByUser(Long userId, Long commentId);
}
