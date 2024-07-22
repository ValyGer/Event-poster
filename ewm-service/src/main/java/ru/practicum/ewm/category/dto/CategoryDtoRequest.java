package ru.practicum.ewm.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDtoRequest {

    @NotBlank
    @Size(max = 50, message = "The name must be no more than 50 characters long")
    private String name;
}
