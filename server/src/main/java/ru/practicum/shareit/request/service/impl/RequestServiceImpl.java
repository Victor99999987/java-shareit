package ru.practicum.shareit.request.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.exception.RequestValidationException;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;

    private final Sort byCreatedDESC = Sort.by(Sort.Direction.DESC, "created");
    private final Sort byId = Sort.by(Sort.Direction.ASC, "id");

    public RequestServiceImpl(UserRepository userRepository, RequestRepository requestRepository,
                              ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    @Override
    public RequestDto add(Long userId, RequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Request request = RequestMapper.toRequest(requestDto);
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());
        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestDto> findAllByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден", userId)));

        List<Request> requests = requestRepository.findAllByRequestor(user, byCreatedDESC);
        List<RequestDto> requestDtos = requests
                .stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.findAllByRequestIn(requests, byId);
        List<ItemDto> itemDtos = items
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());

        for (RequestDto requestDto : requestDtos) {
            List<ItemDto> curItemDtos = itemDtos
                    .stream()
                    .filter(itemDto -> itemDto.getRequestId().equals(requestDto.getId()))
                    .collect(Collectors.toList());
            requestDto.setItems(curItemDtos);
        }

        return requestDtos;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestDto> findAll(Long userId, int from, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        if (from < 0) {
            throw new RequestValidationException("Минимальное значение записи, с которой можно получить данные равно 0");
        }
        if (size <= 0) {
            throw new RequestValidationException("Количество записей на странице должно быть больше 0");
        }
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, byCreatedDESC);
        List<Request> requests = requestRepository.findAllByRequestorNot(user, pageable);
        List<RequestDto> requestDtos = requests
                .stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.findAllByRequestIn(requests, byId);
        List<ItemDto> itemDtos = items
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());

        for (RequestDto requestDto : requestDtos) {
            List<ItemDto> curItemDtos = itemDtos
                    .stream()
                    .filter(itemDto -> itemDto.getRequestId().equals(requestDto.getId()))
                    .collect(Collectors.toList());
            requestDto.setItems(curItemDtos);
        }

        return requestDtos;
    }

    @Transactional(readOnly = true)
    @Override
    public RequestDto findById(Long userId, Long id) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден", userId)));

        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RequestNotFoundException(String.format("Запрос с id %d не найден", id)));
        RequestDto requestDto = RequestMapper.toDto(request);

        List<Item> items = itemRepository.findAllByRequest(request, byId);
        List<ItemDto> itemDtos = items
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
        requestDto.setItems(itemDtos);

        return requestDto;
    }
}
