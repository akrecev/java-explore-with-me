package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findEventsByInitiatorId(Long initiatorId, Pageable pageable);

    Page<Event> findEventsByInitiatorInAndStateInAndCategoryInAndEventDateBetween(
            List<User> users, List<State> states, List<Category> categories,
            LocalDateTime start, LocalDateTime end, Pageable pageable
    );


    @Query("SELECT e FROM Event e " +
            "WHERE (e.state = :state) " +
            "AND (UPPER(e.annotation) LIKE UPPER(CONCAT('%', :text, '%')) " +
            "OR UPPER(e.description) LIKE UPPER(CONCAT('%', :text, '%'))) " +
            "AND (e.category IN :categories OR :categories IS NULL) " +
            "AND (e.paid = :paid OR :paid IS NULL ) " +
            "AND (e.eventDate BETWEEN :start AND :end) " +
            "ORDER BY e.eventDate")
    Page<Event> search(
            State state, String text, List<Category> categories, Boolean paid,
            LocalDateTime start, LocalDateTime end, Pageable pageable
    );

}

