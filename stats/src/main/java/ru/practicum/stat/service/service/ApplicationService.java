package ru.practicum.stat.service.service;

import ru.practicum.stat.service.model.Application;

import java.util.Optional;

public interface ApplicationService {
    Optional<Application> fineByName(String app);

    Optional<Application> add(String app);
}
