package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationMapper;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.errors.NotFoundException;
import ru.practicum.ewm.event.dto.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.event.service.EventStatisticService;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventService eventService;
    private final EventStatisticService eventStatisticService;
    private final EventMapper eventMapper;


    // часть admin

    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> eventList;
        Map<Long, Long> views;
        if (newCompilationDto.getEvents() != null) {
            eventList = eventService.getAllEventsByListId(newCompilationDto.getEvents());
//            views = eventStatisticService.getEventsViews(newCompilationDto.getEvents());
        } else {
            eventList = new ArrayList<>();
        }
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);
        compilation.setEvents(eventList);
        compilation = compilationRepository.save(compilation);
        log.info("Добавлена новая подборка");
        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilation);
        compilationDto.setEvents(eventList.stream().map(eventMapper::toEventShortDto).collect(Collectors.toList()));
        return compilationDto;
    }

    @Transactional
    public void deleteCompilation(Long compId) {
        compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Compilation with id = " + compId + " was not found"));
        compilationRepository.deleteById(compId);
        log.info("Подборка с id = {}, успешно удалена", compId);
    }

    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilationSaved = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Compilation with id = " + compId + " was not found"));
//        Map<Long, Long> views = statService.getEventsViews(events);

        List<Event> eventList = new ArrayList<>();
        if (updateCompilationRequest.getEvents() != null & !updateCompilationRequest.getEvents().isEmpty()) {
//тут просмотры добавить
            eventList = eventService.getAllEventsByListId(updateCompilationRequest.getEvents());
            compilationSaved.setEvents(eventList);
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilationSaved.setIsPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilationSaved.setTitle(updateCompilationRequest.getTitle());
        }
        log.info("Подборка с ID = {} успешно обновлена.", compId);
        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilationSaved);
        compilationDto.setEvents(eventList.stream().map(eventMapper::toEventShortDto).collect(Collectors.toList()));
        return compilationDto;
    }

    // часть public

    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Compilation with id = " + compId + " was not found"));
        List<Event> eventLis = eventService.getAllEventsByListId(compilation.getEvents().stream()
                .map(Event::getId).collect(Collectors.toList()));
//        Map<Long, Long> views = statService.getEventsViews(events);
        log.info("Возвращен пользователь с id = {}", compId);
        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilation);
        compilationDto.setEvents(eventLis.stream().map(eventMapper::toEventShortDto).collect(Collectors.toList()));
        return compilationDto;

    }

    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations = compilationRepository.findAllByIsPinned(pinned, PageRequest.of(from / size, size));

        Set<Event> eventList = new HashSet<>(); // так как не должно быть дублированных элементов!!
        for (Compilation compilation : compilations) {
            eventList.addAll(compilation.getEvents());
        }
        List<Long> eventsId = eventList.stream().map(Event::getId).collect(Collectors.toList());
// получаем просмотры
//        Map<Long, Long> views = statService.getEventsViews(events);

        List<CompilationDto> compilationDtoList = new ArrayList<>();
        for (Compilation compilation : compilations) {
            List<Event> eventLis = eventService.getAllEventsByListId(List.of(compilation.getId()));
            CompilationDto compilationDto = compilationMapper.toCompilationDto(compilation);
            compilationDto.setEvents(eventLis.stream().map(eventMapper::toEventShortDto).collect(Collectors.toList()));
            compilationDtoList.add(compilationDto);
        }
        return compilationDtoList;
    }
}
