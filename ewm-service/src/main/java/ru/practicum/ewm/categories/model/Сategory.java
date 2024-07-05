package ru.practicum.ewm.categories.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories")
public class Сategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
}
