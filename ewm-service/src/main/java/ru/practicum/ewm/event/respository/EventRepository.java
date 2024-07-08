package ru.practicum.ewm.event.respository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select ev " +
            "from Event ev " +
            "where ev.initiator.id = ?1 " +
            "order by ev.id desc")
    List<Event>  findEventsOfUser(Long userId, PageRequest pageRequest);

    Event findEventByIdAndInitiatorId(Long userId, Long eventId);

}
