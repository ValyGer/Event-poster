package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dtoSer.ApplicationDto;
import ru.practicum.dtoSer.ApplicationDtoForHits;
import ru.practicum.dto.ApplicationDtoForHitsInt;
import ru.practicum.model.Application;

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
