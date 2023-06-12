package ru.practicum.shareit.booking.dto;

import java.time.LocalDate;

public class BookingDto {
    private Long id;
    private Long itemId;
    private LocalDate start;
    private LocalDate end;
    private Long userId;
    private String status;
    private String review;
}
