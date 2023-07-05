package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> findAll();

    UserDto findById(Long id);

    UserDto add(UserDto userDto);

    void remove(Long id);

    UserDto update(Long id, UserDto userDto);
}
