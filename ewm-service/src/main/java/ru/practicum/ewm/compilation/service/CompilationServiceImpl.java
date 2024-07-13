package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationMapper;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.errors.NotFoundException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.event.service.EventStatisticService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventService eventService;
    private final EventStatisticService eventStatisticService;

    // часть admin

    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> eventLis;
        Map<Long, Long> views;
        if (newCompilationDto.getEvents() != null) {
            eventLis = eventService.getAllEventsByListId(newCompilationDto.getEvents());
//            views = eventStatisticService.getEventsViews(newCompilationDto.getEvents());
        } else {
            eventLis = new ArrayList<>();
        }

        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);

        log.info("Добавлена новая подборка");
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Transactional
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id = " + compId + " was not found");
        }
        compilationRepository.deleteById(compId);
        log.info("Подборка с id = {}, успешно удалена", compId);
    }

    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        return null;
    }

    // часть public

    public CompilationDto getCompilationById(Long compId) {
        return null;
    }

    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        return null;
    }
}
