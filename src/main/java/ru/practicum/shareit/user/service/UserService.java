package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getAll() {
        return userRepository.getAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto getById(Long id) {
        return UserMapper.toDto(userRepository.getById(id));
    }

    public UserDto add(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toDto(userRepository.add(user));
    }

    public UserDto remove(Long id) {
        return UserMapper.toDto(userRepository.remove(id));
    }

    public UserDto update(Long id, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User oldUser = userRepository.getById(id);
        user.setId(id);
        if (user.getName() == null) {
            user.setName(oldUser.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(oldUser.getEmail());
        }
        return UserMapper.toDto(userRepository.update(user));
    }
}
