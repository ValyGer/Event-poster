package ru.practicum.service;

import ru.practicum.model.Application;

import java.util.Optional;

public interface ApplicationService {
    Optional<Application> fineByName(String app);

    Optional<Application> add(String app);
}
