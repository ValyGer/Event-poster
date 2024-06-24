package ru.practicum.stat.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.stat.service.dto.ApplicationDto;
import ru.practicum.stat.service.dto.ApplicationDtoForHits;
import ru.practicum.stat.service.dto.ApplicationDtoForHitsInt;
import ru.practicum.stat.service.model.Application;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {
    @Mapping(target = "id", ignore = true)
    Application toApplication(ApplicationDto applicationDto);

    @Mapping(source = "hits", target = "hits", qualifiedBy = LongHitsToIntHits.class)
    ApplicationDtoForHitsInt toApplicationDtoForHitsInt(ApplicationDtoForHits applicationDtoForHits);

    @LongHitsToIntHits
    static int hitsLongToInt(Long hits) {
        return Math.toIntExact(hits);
    }
}
