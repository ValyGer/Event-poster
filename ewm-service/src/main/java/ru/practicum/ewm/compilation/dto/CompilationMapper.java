package ru.practicum.ewm.compilation.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.compilation.model.Compilation;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "pinned", target = "isPinned")
    @Mapping(target = "events", ignore = true)
    Compilation toCompilation(NewCompilationDto newCompilationDto);

    @Mapping(source = "isPinned", target = "pinned")
    @Mapping(target = "events", ignore = true)
    CompilationDto toCompilationDto(Compilation compilation);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "pinned", target = "isPinned")
    @Mapping(target = "events", ignore = true)
    Compilation toCompilationForUpdate(UpdateCompilationRequest updateCompilationRequest);
}
