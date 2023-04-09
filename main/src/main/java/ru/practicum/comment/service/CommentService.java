package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentPublicDto;

import java.util.List;

public interface CommentService {
    CommentDto create(Long userId, Long eventId, CommentDto newComment);

    List<CommentDto> getAllByUser(Long userId, Integer from, Integer size);

    List<CommentDto> getAllByEvent(Long eventId, Integer from, Integer size);

    List<CommentPublicDto> getAllByEventPublic(Long eventId, Integer from, Integer size);

    CommentDto getById(Long userId, Long commentId);

    CommentDto update(Long userId, Long commentId, CommentDto updateComment);

    void deleteByUser(Long userId, Long commentId);
}
