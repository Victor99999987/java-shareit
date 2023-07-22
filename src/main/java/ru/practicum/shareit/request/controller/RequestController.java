package ru.practicum.shareit.request.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequestMapping("/requests")
public class RequestController {
    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public RequestDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @NotNull @Valid @RequestBody RequestDto requestDto) {
        return requestService.add(userId, requestDto);
    }

    @GetMapping
    public List<RequestDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.findAllByUserId(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size) {
        return requestService.findAll(userId, from, size);
    }

    @GetMapping("/{id}")
    public RequestDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long id) {
        return requestService.findById(userId, id);
    }

}
