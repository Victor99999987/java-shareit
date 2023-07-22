package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 1, max = 200, message = "Имя должна быть до 200 символов")
    private String name;
    @NotNull(message = "Email не указан")
    @Email(message = "Неверный Email")
    @Size(min = 1, max = 200, message = "Email должен быть до 200 символов")
    private String email;
}
