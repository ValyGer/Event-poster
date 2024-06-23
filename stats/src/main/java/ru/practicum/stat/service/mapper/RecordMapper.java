package ru.practicum.stat.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.stat.service.dto.RecordDto;
import ru.practicum.stat.service.model.Record;

@Mapper(componentModel = "spring")
public interface RecordMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "app", ignore = true)
    @Mapping(target = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Record toRecord(RecordDto recordDto);
}
