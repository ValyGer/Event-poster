package ru.practicum.stats.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.stats.EndpointHit;

@Mapper(componentModel = "spring")
public interface HitMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Hit toHit(EndpointHit endpointHit);
}
