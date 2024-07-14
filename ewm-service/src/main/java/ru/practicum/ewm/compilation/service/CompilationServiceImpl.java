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
import ru.practicum.ewm.event.dto.EventShortDto;
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
        if (newCompilationDto.getEvents() != null) {
            eventList = eventService.getAllEventsByListId(newCompilationDto.getEvents());
        } else {
            eventList = new ArrayList<>();
        }

        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);
        compilation.setEvents(eventList);
        compilation = compilationRepository.save(compilation);
        log.info("Добавлена новая подборка");

        return mapperEventsAndSetView(compilation, eventList);
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

        List<Event> eventList = new ArrayList<>();
        if (updateCompilationRequest.getEvents() != null & !updateCompilationRequest.getEvents().isEmpty()) {
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
        return mapperEventsAndSetView(compilationSaved, eventList);
    }

    // часть public

    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Compilation with id = " + compId + " was not found"));
        List<Event> eventList = eventService.getAllEventsByListId(compilation.getEvents().stream()
                .map(Event::getId).collect(Collectors.toList()));
        log.info("Возвращен пользователь с id = {}", compId);
        return mapperEventsAndSetView(compilation, eventList);

    }

    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations = compilationRepository.findAllByIsPinned(pinned, PageRequest.of(from / size, size));

        Set<Event> eventSet = new HashSet<>(); // так как не должно быть дублированных элементов!!
        for (Compilation compilation : compilations) {
            eventSet.addAll(compilation.getEvents());
        }
        List<Long> eventsId = eventSet.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> views = eventStatisticService.getEventsViews(eventsId);

        List<CompilationDto> compilationDtoList = new ArrayList<>();
        for (Compilation compilation : compilations) {
            List<Event> eventList = eventService.getAllEventsByListId(compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList()));
            List<EventShortDto> eventShortDtoList = eventList.stream().map(eventMapper::toEventShortDto).collect(Collectors.toList());
            for (EventShortDto eventShortDto : eventShortDtoList) {
                if (views.get(eventShortDto.getId()) == null) {
                    eventShortDto.setViews(views.get(0));
                } else {
                    eventShortDto.setViews(views.get(eventShortDto.getId()));
                }
            }
            CompilationDto compilationDto = compilationMapper.toCompilationDto(compilation);
            compilationDto.setEvents(eventShortDtoList);
            compilationDtoList.add(compilationDto);
        }
        return compilationDtoList;
    }


    private CompilationDto mapperEventsAndSetView(Compilation compilation, List<Event> eventList) {
        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilation);
        List<EventShortDto> eventsShortDto = eventList.stream().map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());

        Map<Long, Long> views = eventStatisticService.getEventsViews(eventsShortDto.stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toList()));
        for (EventShortDto eventShortDto : eventsShortDto) {
            eventShortDto.setViews(views.get(eventShortDto.getId()));
        }
        compilationDto.setEvents(eventsShortDto);
        return compilationDto;
    }
}
