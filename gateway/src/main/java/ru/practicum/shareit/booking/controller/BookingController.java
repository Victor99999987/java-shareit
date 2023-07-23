package ru.practicum.shareit.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDtoIn;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @NotNull @Valid @RequestBody BookingDtoIn bookingDtoIn) {
        return bookingClient.add(userId, bookingDtoIn);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long id,
                                         @RequestParam boolean approved) {
        return bookingClient.update(userId, id, approved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long id) {
        return bookingClient.findById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "10") int size) {
        return bookingClient.findAllByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        return bookingClient.findAllByOwnerId(userId, state, from, size);
    }

}
