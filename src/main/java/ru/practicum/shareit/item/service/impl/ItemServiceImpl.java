package ru.practicum.shareit.item.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemValidationException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;
    private final Sort byStartDESC = Sort.by(Sort.Direction.DESC, "start");
    private final Sort byStartASC = Sort.by(Sort.Direction.ASC, "start");
    private final Sort byId = Sort.by(Sort.Direction.ASC, "id");
    private final Sort byCreatedASC = Sort.by(Sort.Direction.ASC, "created");

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository, RequestRepository requestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.requestRepository = requestRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> findAllByUserId(Long userId, int from, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден", userId)));

        if (from < 0) {
            throw new ItemValidationException("Минимальное значение записи, с которой можно получить данные равно 0");
        }
        if (size <= 0) {
            throw new ItemValidationException("Количество записей на странице должно быть больше 0");
        }
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, byId);

        List<Item> items = itemRepository.findAllByOwner(user, pageable);
        List<ItemDto> itemsDto = items
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
        List<Booking> lastBookings = bookingRepository.findAllByItemInAndStartBeforeAndStatus(items,
                LocalDateTime.now(), BookingStatus.APPROVED, byStartDESC);
        List<Booking> nextBookings = bookingRepository.findAllByItemInAndStartAfterAndStatus(items,
                LocalDateTime.now(), BookingStatus.APPROVED, byStartASC);
        List<Comment> comments = commentRepository.findAllByItemIn(items, byCreatedASC);

        for (ItemDto itemDto : itemsDto) {
            List<Booking> curLastBooking = lastBookings
                    .stream()
                    .filter(booking -> booking.getItem().getId().equals(itemDto.getId()))
                    .collect(Collectors.toList());
            List<Booking> curNextBooking = nextBookings
                    .stream()
                    .filter(booking -> booking.getItem().getId().equals(itemDto.getId()))
                    .collect(Collectors.toList());
            if (!curLastBooking.isEmpty()) {
                itemDto.setLastBooking(BookingMapper.toDtoOutToItem(curLastBooking.get(0)));
            }
            if (!curNextBooking.isEmpty()) {
                itemDto.setNextBooking(BookingMapper.toDtoOutToItem(curNextBooking.get(0)));
            }

            List<CommentDto> curCommentsDto = comments
                    .stream()
                    .filter(comment -> comment.getItem().getId().equals(itemDto.getId()))
                    .map(CommentMapper::toDto)
                    .collect(Collectors.toList());
            itemDto.setComments(curCommentsDto);
        }
        return itemsDto;
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto findById(Long userId, Long id) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id %d не найдена", id)));

        ItemDto itemDto = ItemMapper.toDto(item);

        if (item.getOwner().getId().equals(userId)) {
            Booking lastBooking = bookingRepository.findFirstBookingByItemAndStartBeforeAndStatus(item,
                    LocalDateTime.now(), BookingStatus.APPROVED, byStartDESC);
            Booking nextBooking = bookingRepository.findFirstBookingByItemAndStartAfterAndStatus(item,
                    LocalDateTime.now(), BookingStatus.APPROVED, byStartASC);
            if (lastBooking != null) {
                itemDto.setLastBooking(BookingMapper.toDtoOutToItem(lastBooking));
            }
            if (nextBooking != null) {
                itemDto.setNextBooking(BookingMapper.toDtoOutToItem(nextBooking));
            }
        }

        List<CommentDto> commentsDto = commentRepository.findAllByItem(item, byCreatedASC)
                .stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
        itemDto.setComments(commentsDto);

        return itemDto;
    }

    @Transactional
    @Override
    public ItemDto add(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);

        Long requestId = itemDto.getRequestId();
        if (requestId != null) {
            Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(String.format("Запрос с id %d не найден", requestId)));
            item.setRequest(request);
        }

        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public void remove(Long userId, Long id) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id %d не найдена", id)));
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            log.info(String.format("У вещи с id=%d другой владелец", id));
            throw new ItemNotFoundException(String.format("У вещи с id=%d другой владелец", id));
        }
        itemRepository.deleteById(id);
    }

    @Transactional
    @Override
    public ItemDto update(Long userId, Long id, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Item oldItem = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id %d не найдена", id)));
        Item item = ItemMapper.toItem(itemDto);
        item.setId(id);
        item.setOwner(user);
        if (item.getName() == null) {
            item.setName(oldItem.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(oldItem.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(oldItem.getAvailable());
        }
        return ItemMapper.toDto(itemRepository.save(item));
    }


    @Override
    public List<ItemDto> search(String text, int from, int size) {
        if (text.isEmpty()) {
            return List.of();
        }

        if (from < 0) {
            throw new ItemValidationException("Минимальное значение записи, с которой можно получить данные равно 0");
        }
        if (size <= 0) {
            throw new ItemValidationException("Количество записей на странице должно быть больше 0");
        }
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, byId);

        return itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(text,
                        text, true, pageable)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id %d не найдена", itemId)));
        Booking booking = bookingRepository.findFirstBookingByItemAndBookerAndStatusAndEndBefore(item, user,
                        BookingStatus.APPROVED, LocalDateTime.now(), byStartDESC)
                .orElseThrow(() -> new ItemValidationException("Вещь с id %d нельзя комментировать до окончания аренды"));

        Comment comment = CommentMapper.toCommment(commentDto);
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toDto(commentRepository.save(comment));
    }
}
