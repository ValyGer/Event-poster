package ru.practicum.ewm.request.model;

import lombok.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;
    @Enumerated(value = EnumType.STRING)
    private RequestStatus status;

    public Request(Event event, User requester) {
        this.createdOn = LocalDateTime.now();
        this.event = event;
        this.requester = requester;
        this.status = RequestStatus.PENDING;
    }
}
