package ru.practicum.shareit.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id) {
        return userClient.findById(id);
    }

    @PostMapping
    public ResponseEntity<Object> add(@NotNull @Valid @RequestBody UserDto userDto) {
        return userClient.add(userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> remove(@PathVariable Long id) {
        return userClient.remove(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @NotNull @RequestBody UserDto userDto) {
        return userClient.update(id, userDto);
    }
}
