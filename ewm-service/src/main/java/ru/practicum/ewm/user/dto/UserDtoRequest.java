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
    @Size(min = 2, max = 250, message = "The name must be between 2 and 250 characters long")
    private String name;
    @Email
    @NotBlank
    @Size(min = 6, max = 254, message = "The email must be between 6 and 254 characters long")
    private String email;
}
