package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
public class ItemDto {
    private Long id;
    @NotBlank(message = "Название не должно быть пустым")
    private String name;
    @NotBlank(message = "Описание не должно быть пустым")
    private String description;
    @NotNull(message = "Статус доступности должен быть указан")
    private Boolean available;
}
