package ru.practicum.shareit.user.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public UserDto add(@NotNull @Valid @RequestBody UserDto userDto) {
        return userService.add(userDto);
    }

    @DeleteMapping("/{id}")
    public UserDto remove(@PathVariable Long id) {
        return userService.remove(id);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id, @NotNull @RequestBody UserDto userDto) {
        return userService.update(id, userDto);
    }
}
