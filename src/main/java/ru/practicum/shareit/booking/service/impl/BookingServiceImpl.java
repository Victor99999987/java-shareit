package ru.practicum.shareit.booking.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingValidationException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final Sort byStartDESC = Sort.by(Sort.Direction.DESC, "start");

    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    @Override
    public BookingDtoOut add(Long userId, BookingDtoIn bookingDtoIn) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден", userId)));

        Item item = itemRepository.findById(bookingDtoIn.getItemId())
                .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id %d не найдена", bookingDtoIn.getItemId())));
        if (!item.getAvailable()) {
            throw new ItemValidationException(String.format("Вещь с id %d не доступна для бронирования", item.getId()));
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new ItemNotFoundException("Владелец вещи не может оформлять её бронирование");
        }
        if (bookingDtoIn.getStart().isAfter(bookingDtoIn.getEnd())) {
            throw new ItemValidationException("Дата начала бронирования позже окончания бронирования");
        }
        if (bookingDtoIn.getStart().equals(bookingDtoIn.getEnd())) {
            throw new ItemValidationException("Дата начала и окончания бронирования совпадают");
        }
        Booking booking = BookingMapper.toBooking(bookingDtoIn);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toDtoOut(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDtoOut update(Long userId, Long id, boolean approved) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден", userId)));

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(String.format("Бронирование с id %d не найдено", id)));

        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new BookingNotFoundException("Подтверждать или отклонять бронирование может только владелец вещи");
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BookingValidationException("Бронирование уже рассмотрено");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.toDtoOut(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDtoOut findById(Long userId, Long id) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден", userId)));

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(String.format("Бронирование с id %d не найдено", id)));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new BookingNotFoundException("Просмотреть информацию о бронировании может только автор или владелец вещи");
        }

        return BookingMapper.toDtoOut(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoOut> findAllByUserId(Long userId, String stateIn) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден", userId)));

        State state;
        try {
            state = State.valueOf(stateIn);
        } catch (IllegalArgumentException e) {
            throw new BookingValidationException(String.format("Unknown state: %s", stateIn));
        }

        List<Booking> result = new ArrayList<>();
        switch (state) {
            case ALL:
                result = bookingRepository.findAllByBooker(user, byStartDESC);
                break;
            case CURRENT:
                result = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), byStartDESC);
                break;
            case PAST:
                result = bookingRepository.findAllByBookerAndEndBefore(user, LocalDateTime.now(), byStartDESC);
                break;
            case FUTURE:
                result = bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now(), byStartDESC);
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerAndStatus(user, BookingStatus.WAITING, byStartDESC);
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerAndStatus(user, BookingStatus.REJECTED, byStartDESC);
                break;
        }

        return result
                .stream()
                .map(BookingMapper::toDtoOut)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoOut> findAllByOwnerId(Long ownerId, String stateIn) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден", ownerId)));

        State state;
        try {
            state = State.valueOf(stateIn);
        } catch (IllegalArgumentException e) {
            throw new BookingValidationException(String.format("Unknown state: %s", stateIn));
        }

        List<Booking> result = new ArrayList<>();
        switch (state) {
            case ALL:
                result = bookingRepository.findAllByItem_Owner(owner, byStartDESC);
                break;
            case CURRENT:
                result = bookingRepository.findAllByItem_OwnerAndStartBeforeAndEndAfter(owner, LocalDateTime.now(),
                        LocalDateTime.now(), byStartDESC);
                break;
            case PAST:
                result = bookingRepository.findAllByItem_OwnerAndEndBefore(owner, LocalDateTime.now(), byStartDESC);
                break;
            case FUTURE:
                result = bookingRepository.findAllByItem_OwnerAndStartAfter(owner, LocalDateTime.now(), byStartDESC);
                break;
            case WAITING:
                result = bookingRepository.findAllByItem_OwnerAndStatus(owner, BookingStatus.WAITING, byStartDESC);
                break;
            case REJECTED:
                result = bookingRepository.findAllByItem_OwnerAndStatus(owner, BookingStatus.REJECTED, byStartDESC);
                break;
        }

        return result
                .stream()
                .map(BookingMapper::toDtoOut)
                .collect(Collectors.toList());
    }

}
