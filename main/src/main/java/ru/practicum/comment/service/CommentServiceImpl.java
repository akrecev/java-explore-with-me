package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentPublicDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.utility.MyPageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.comment.mapper.CommentMapper.toComment;
import static ru.practicum.comment.mapper.CommentMapper.toCommentDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;


    @Override
    @Transactional
    public CommentDto create(Long userId, Long eventId, CommentDto newComment) {
        User commentator = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(
                        "User with id=" + userId + " was not found"
                ));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException(
                        "Event with id=" + newComment.getEventId() + " was not found"
                ));

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new BadRequestException("Event id=" + eventId + " was not published");
        }

        Comment comment = commentRepository.save(toComment(newComment, commentator, event));

        return toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getAllCommentsByUser(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));

        return commentRepository.findCommentsByCommentatorId(userId, new MyPageRequest(from, size))
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllCommentsByEvent(Long eventId, Integer from, Integer size) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));

        return commentRepository.findCommentsByEventId(eventId, new MyPageRequest(from, size))
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentPublicDto> getAllCommentsByEventPublic(Long eventId, Integer from, Integer size) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));

        return commentRepository.findCommentsByEventId(eventId, new MyPageRequest(from, size))
                .stream()
                .map(CommentMapper::toCommentPublicDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Long userId, Long commentId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException("Comment with id=" + commentId + " was not found"));

        return toCommentDto(comment);
    }

    @Override
    public CommentDto updateCommentByUser(Long userId, Long commentId, CommentDto updateCommentDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException("Comment with id=" + commentId + " was not found"));

        if (!comment.getCommentator().getId().equals(userId)) {
            throw new ConflictException("User id=" + userId + " is not owner by comment id=" + commentId);
        }

        if (updateCommentDto.getDescription() != null
                && !updateCommentDto.getDescription().equals(comment.getDescription())) {
            comment.setDescription(updateCommentDto.getDescription());
        }

        comment.setCreated(LocalDateTime.now());
        Comment updatedComment = commentRepository.save(comment);

        return toCommentDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteCommentByUser(Long userId, Long commentId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException("Comment with id=" + commentId + " was not found"));

        if (!comment.getCommentator().getId().equals(userId)) {
            throw new ConflictException("User id=" + userId + " is not owner by comment id=" + commentId);
        }

        commentRepository.deleteById(commentId);
    }
}
