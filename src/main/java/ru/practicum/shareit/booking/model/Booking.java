package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

public class Booking {
    private Long id;
    private Item item;
    private LocalDate start;
    private LocalDate end;
    private User booker;
    private BookingStatus status;
    private String review;
}

