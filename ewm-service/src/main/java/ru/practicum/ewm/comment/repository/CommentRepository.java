package ru.practicum.ewm.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByEventOrderByEvent(Event event);
}
