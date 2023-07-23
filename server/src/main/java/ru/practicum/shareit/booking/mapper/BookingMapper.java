package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingDtoOutToItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class BookingMapper {
    public static BookingDtoOut toDtoOut(Booking booking) {
        return BookingDtoOut.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .build();
    }

    public static BookingDtoOutToItem toDtoOutToItem(Booking booking) {
        return BookingDtoOutToItem.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static Booking toBooking(BookingDtoIn bookingDtoIn) {
        Item item = Item.builder()
                .id(bookingDtoIn.getItemId())
                .build();
        return Booking.builder()
                .item(item)
                .start(bookingDtoIn.getStart())
                .end(bookingDtoIn.getEnd())
                .build();
    }
}
