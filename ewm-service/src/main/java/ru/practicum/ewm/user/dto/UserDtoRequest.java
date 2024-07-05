package ru.practicum.ewm.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserDtoRequest {

    @NotBlank
    @Size(min = 2, max = 250, message = "Имя должно быть длинной от 2 до 250 символов")
    private String name;
    @Email
    @NotBlank
    @Size(min = 2, max = 250, message = "Имя должно быть длинной от 2 до 250 символов")
    private String email;
}
