package ru.practicum.stat.service.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stat.service.model.Application;
import ru.practicum.stat.service.repository.ApplicationRepository;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;

    public Optional<Application> fineByName(String app) {
        log.info("Выполнение поиска сервиса с именем {}", app);
        return applicationRepository.findByApp(app);
    }

    public Optional<Application> add(String app) {
        log.info("Сохранение сервиса с именем {}", app);
        return Optional.of(applicationRepository.save(new Application(app)));
    }
}
