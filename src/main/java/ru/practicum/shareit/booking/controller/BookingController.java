package ru.practicum.shareit.booking.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDtoOut add(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @NotNull @Valid @RequestBody BookingDtoIn bookingDtoIn) {
        return bookingService.add(userId, bookingDtoIn);
    }

    @PatchMapping("/{id}")
    public BookingDtoOut update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @PathVariable Long id,
                                @RequestParam boolean approved) {
        return bookingService.update(userId, id, approved);
    }

    @GetMapping("/{id}")
    public BookingDtoOut findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long id) {
        return bookingService.findById(userId, id);
    }

    @GetMapping
    public List<BookingDtoOut> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllByOwnerId(userId, state);
    }

}
