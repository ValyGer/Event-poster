package ru.practicum.ewm.comment.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    @Size(min = 3, max = 2000)
    private String text;
    private String authorName;
    private String eventId;
    private LocalDateTime create;

    public CommentDto(String text) {
        this.text = text;
    }
}
