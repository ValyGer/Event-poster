package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.errors.NotFoundException;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserDtoRequest;
import ru.practicum.ewm.user.dto.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public List<UserDto> getAllUsers(List<Long> usersId, Integer from, Integer size) {
        List<User> users;
        if (usersId == null || usersId.isEmpty()) {
            users = userRepository.findAll(PageRequest.of(from / size, size)).getContent();
        } else {
            users = userRepository.findAllByIdIn(usersId, PageRequest.of(from / size, size));
        }
        log.info("Список пользователей с id = {} успешно выдан", usersId);
        return users.stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto createUser(UserDtoRequest userDtoRequest) {
        log.info("Пользователь {} успешно добавлен", userDtoRequest);
        return userMapper.toUserDto(userRepository.save(userMapper.toUser(userDtoRequest)));
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id = " + userId + " was not found");
        }
        userRepository.deleteById(userId);
        log.info("Пользователь с id = {}, успешно удален", userId);
    }

    public User getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        throw new NotFoundException("User with id = " + userId + " was not found");
    }
}
