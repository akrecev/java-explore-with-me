package ru.practicum.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.model.Comment;
import ru.practicum.utility.MyPageRequest;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findCommentsByCommentatorId(Long userId, MyPageRequest myPageRequest);

    Page<Comment> findCommentsByEventId(Long eventId, MyPageRequest myPageRequest);
}

