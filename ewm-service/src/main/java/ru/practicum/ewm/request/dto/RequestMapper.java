package ru.practicum.ewm.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.request.model.Request;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(source = "requester.id", target = "requester")
    @Mapping(source = "event.id", target = "event")
    ParticipationRequestDto toParticipationRequestDto(Request request);

}
