package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BookingDtoOutToItem {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private String status;
    private Long bookerId;
}
