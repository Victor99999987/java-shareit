package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker(User booker, Pageable pageable);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User booker, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerAndEndBefore(User booker, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerAndStartAfter(User booker, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBookerAndStatus(User booker, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItem_Owner(User owner, Pageable pageable);

    List<Booking> findAllByItem_OwnerAndStartBeforeAndEndAfter(User owner, LocalDateTime start, LocalDateTime end,
                                                               Pageable pageable);

    List<Booking> findAllByItem_OwnerAndEndBefore(User owner, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItem_OwnerAndStartAfter(User owner, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItem_OwnerAndStatus(User owner, BookingStatus status, Pageable pageable);

    Booking findFirstBookingByItemAndStartBeforeAndStatus(Item item, LocalDateTime start, BookingStatus status,
                                                          Sort sort);

    Booking findFirstBookingByItemAndStartAfterAndStatus(Item item, LocalDateTime start, BookingStatus status,
                                                         Sort sort);

    List<Booking> findAllByItemInAndStartBeforeAndStatus(List<Item> items, LocalDateTime start, BookingStatus status,
                                                         Sort sort);

    List<Booking> findAllByItemInAndStartAfterAndStatus(List<Item> items, LocalDateTime start, BookingStatus status,
                                                        Sort sort);

    Optional<Booking> findFirstBookingByItemAndBookerAndStatusAndEndBefore(Item item, User booker, BookingStatus status,
                                                                           LocalDateTime end, Sort sort);
}
