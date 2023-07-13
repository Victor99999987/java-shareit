package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    private Long id;
    @NotBlank(message = "Описание не должно быть пустым")
    @Size(min = 1, max = 1000, message = "Описание должно быть до 1000 символов")
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
