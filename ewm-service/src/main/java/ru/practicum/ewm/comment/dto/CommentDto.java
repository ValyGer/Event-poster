package ru.practicum.ewm.comment.dto;

import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    @Size(min = 3, max = 2000)
    private String text;
    private String authorName;
    private String eventId;
    private String create;
}
