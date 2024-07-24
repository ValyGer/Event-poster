package ru.practicum.ewm.comment.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentDtoPublic {
    private String text;
    private String eventId;
    private LocalDateTime create;
}
