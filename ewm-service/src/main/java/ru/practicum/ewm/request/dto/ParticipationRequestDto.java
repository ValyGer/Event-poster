package ru.practicum.ewm.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {

    private long id;
    @NotNull
    private String created;
    @NotNull
    private long event;
    @NotNull
    private long requester;
    @NotBlank
    private String status;
}
