package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserDtoRequest;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers(List<Long> usersId, Integer from, Integer size);

    UserDto createUser(UserDtoRequest userDtoRequest);

    void deleteUser(Long userId);

    User getUserById(Long userId);
}
