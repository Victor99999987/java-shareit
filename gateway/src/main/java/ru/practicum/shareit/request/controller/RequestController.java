package ru.practicum.shareit.request.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
@RestController
@RequestMapping("/requests")
public class RequestController {
    private final RequestClient requestClient;

    public RequestController(RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @NotNull @Valid @RequestBody RequestDto requestDto) {
        return requestClient.add(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.findAllByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "10") int size) {
        return requestClient.findAll(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long id) {
        return requestClient.findById(userId, id);
    }

}
