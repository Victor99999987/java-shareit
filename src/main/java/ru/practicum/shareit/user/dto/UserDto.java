package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
    @NotNull(message = "Email не указан")
    @Email(message = "Неверный Email")
    private String email;
}
