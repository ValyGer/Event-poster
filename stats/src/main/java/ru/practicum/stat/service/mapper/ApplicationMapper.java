package ru.practicum.stat.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.stat.service.dto.ApplicationDto;
import ru.practicum.stat.service.model.Application;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {
    @Mapping(target = "id", ignore = true)
    Application toApplication(ApplicationDto applicationDto);
}
