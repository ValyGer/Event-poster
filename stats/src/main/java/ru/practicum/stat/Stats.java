package ru.practicum.stat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class Stats {
    public static void main(String[] args) {
        SpringApplication.run(Stats.class, args);
    }
}
