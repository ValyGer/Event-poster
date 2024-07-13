package ru.practicum.ewm.event.respository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;

import java.util.List;
import java.util.Optional;

import static ru.practicum.ewm.event.model.EventState.PUBLISHED;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    @Query("select ev " +
            "from Event ev " +
            "where ev.initiator.id = ?1 " +
            "order by ev.id desc")
    List<Event> findEventsOfUser(Long userId, PageRequest pageRequest);

    Optional<Event> findByIdAndInitiatorId(Long userId, Long eventId);

    Optional<Event> findByIdAndState(Long id, EventState state);
}
