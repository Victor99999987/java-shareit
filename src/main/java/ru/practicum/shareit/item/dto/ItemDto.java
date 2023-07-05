package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDtoOutToItem;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@Builder
public class ItemDto {
    private Long id;
    @NotBlank(message = "Название не должно быть пустым")
    @Size(min = 1, max = 200, message = "Название должно быть до 200 символов")
    private String name;
    @NotBlank(message = "Описание не должно быть пустым")
    @Size(min = 1, max = 1000, message = "Описание должно быть до 200 символов")
    private String description;
    @NotNull(message = "Статус доступности должен быть указан")
    private Boolean available;
    private BookingDtoOutToItem lastBooking;
    private BookingDtoOutToItem nextBooking;
    private List<CommentDto> comments;
}
