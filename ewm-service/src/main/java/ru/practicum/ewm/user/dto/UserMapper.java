package ru.practicum.ewm.user.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toUser(UserDtoRequest userDtoRequest);

    UserDto toUserDto(User user);
}
