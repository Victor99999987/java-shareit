package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Builder
public class BookingDtoIn {
    @NotNull(message = "id вещи бронирования должен быть указан")
    private Long itemId;
    @NotNull(message = "Дата и время начала бронирования должна быть указана")
    @FutureOrPresent(message = "Дата и время начала бронирования не должна быть в прошлом")
    private LocalDateTime start;
    @NotNull(message = "Дата и время окончания бронирования должна быть указана")
    @FutureOrPresent(message = "Дата и время окончания бронирования не должна быть в прошлом")
    private LocalDateTime end;
}
