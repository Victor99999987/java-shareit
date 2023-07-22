package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto add(Long userId, RequestDto requestDto);

    List<RequestDto> findAllByUserId(Long userId);

    List<RequestDto> findAll(Long userId, int from, int size);

    RequestDto findById(Long userId, Long id);
}
