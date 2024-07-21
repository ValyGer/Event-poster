package ru.practicum.ewm;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ViewStats {

    private String app;
    private String uri;
    private long hits;
}
