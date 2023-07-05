package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.List;

public interface BookingService {
    BookingDtoOut add(Long userId, BookingDtoIn bookingDtoIn);

    BookingDtoOut update(Long userId, Long id, boolean approved);

    BookingDtoOut findById(Long userId, Long id);

    List<BookingDtoOut> findAllByUserId(Long userId, String state);

    List<BookingDtoOut> findAllByOwnerId(Long ownerId, String state);
}
