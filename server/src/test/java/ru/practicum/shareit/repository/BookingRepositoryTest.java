package ru.practicum.shareit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class BookingRepositoryTest {

    private final Sort byId = Sort.by(Sort.Direction.ASC, "id");
    private final Pageable pageable = PageRequest.of(0, 10, byId);
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    BookingRepository bookingRepository;
    private User user;
    private User user2;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("user")
                .email("user@user.com")
                .build();
        user2 = User.builder()
                .name("user2")
                .email("user2@user2.com")
                .build();
        item = Item.builder()
                .name("скотч")
                .description("хороший")
                .owner(user)
                .available(true)
                .build();
        booking = Booking.builder()
                .status(BookingStatus.APPROVED)
                .item(item)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().plusDays(5))
                .booker(user)
                .build();

        user = userRepository.save(user);
        user2 = userRepository.save(user2);

        item = itemRepository.save(item);

        booking = bookingRepository.save(booking);
    }

    @Test
    public void findAllByBooker_whenBookingNotFound_ThenReturnEmptyList() {
        List<Booking> result = bookingRepository.findAllByBooker(user2, pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllByBooker_whenBookingFound_ThenReturnList() {
        List<Booking> result = bookingRepository.findAllByBooker(user, pageable);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getBooker().getName(), user.getName());
    }

    @Test
    public void findAllByBookerAndStartBeforeAndEndAfter_whenBookingNotFound_ThenReturnEmptyList() {
        List<Booking> result = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user,
                LocalDateTime.now().minusDays(10), LocalDateTime.now().plusDays(10), pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllByBookerAndStartBeforeAndEndAfter_whenBookingFound_ThenReturnList() {
        List<Booking> result = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user,
                LocalDateTime.now(), LocalDateTime.now(), pageable);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getBooker().getName(), user.getName());
        assertTrue(result.get(0).getStart().isBefore(LocalDateTime.now()));
        assertTrue(result.get(0).getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    public void findAllByBookerAndEndBefore_whenBookingNotFound_ThenReturnEmptyList() {
        List<Booking> result = bookingRepository.findAllByBookerAndEndBefore(user,
                LocalDateTime.now(), pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllByBookerAndEndBefore_whenBookingFound_ThenReturnList() {
        List<Booking> result = bookingRepository.findAllByBookerAndEndBefore(user,
                LocalDateTime.now().plusDays(10), pageable);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getBooker().getName(), user.getName());
        assertTrue(result.get(0).getEnd().isBefore(LocalDateTime.now().plusDays(10)));
    }

    @Test
    public void findAllByBookerAndStartAfter_whenBookingNotFound_ThenReturnEmptyList() {
        List<Booking> result = bookingRepository.findAllByBookerAndStartAfter(user,
                LocalDateTime.now(), pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllByBookerAndStartAfter_whenBookingFound_ThenReturnList() {
        List<Booking> result = bookingRepository.findAllByBookerAndStartAfter(user,
                LocalDateTime.now().minusDays(10), pageable);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getBooker().getName(), user.getName());
        assertTrue(result.get(0).getStart().isAfter(LocalDateTime.now().minusDays(10)));
    }

    @Test
    public void findAllByBookerAndStatus_whenBookingNotFound_ThenReturnEmptyList() {
        List<Booking> result = bookingRepository.findAllByBookerAndStatus(user,
                BookingStatus.WAITING, pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllByBookerAndStatus_whenBookingFound_ThenReturnList() {
        List<Booking> result = bookingRepository.findAllByBookerAndStatus(user,
                BookingStatus.APPROVED, pageable);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getBooker().getName(), user.getName());
        assertEquals(result.get(0).getStatus(), BookingStatus.APPROVED);
    }

    @Test
    public void findAllByItem_Owner_whenBookingNotFound_ThenReturnEmptyList() {
        List<Booking> result = bookingRepository.findAllByItem_Owner(user2,
                pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllByItem_Owner_whenBookingFound_ThenReturnList() {
        List<Booking> result = bookingRepository.findAllByItem_Owner(user,
                pageable);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getBooker().getName(), user.getName());
    }

    @Test
    public void findAllByItem_OwnerAndStartBeforeAndEndAfter_whenBookingNotFound_ThenReturnEmptyList() {
        List<Booking> result = bookingRepository.findAllByItem_OwnerAndStartBeforeAndEndAfter(user2,
                LocalDateTime.now(), LocalDateTime.now(), pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllByItem_OwnerAndStartBeforeAndEndAfter_whenBookingFound_ThenReturnList() {
        List<Booking> result = bookingRepository.findAllByItem_OwnerAndStartBeforeAndEndAfter(user,
                LocalDateTime.now(), LocalDateTime.now(), pageable);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getBooker().getName(), user.getName());
        assertTrue(result.get(0).getStart().isBefore(LocalDateTime.now()));

    }

    @Test
    public void findAllByItem_OwnerAndEndBefore_whenBookingNotFound_ThenReturnEmptyList() {
        List<Booking> result = bookingRepository.findAllByItem_OwnerAndEndBefore(user,
                LocalDateTime.now(), pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllByItem_OwnerAndEndBefore_whenBookingFound_ThenReturnList() {
        List<Booking> result = bookingRepository.findAllByItem_OwnerAndEndBefore(user,
                LocalDateTime.now().plusDays(10), pageable);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getBooker().getName(), user.getName());
        assertTrue(result.get(0).getEnd().isBefore(LocalDateTime.now().plusDays(10)));
    }

    @Test
    public void findAllByItem_OwnerAndStartAfter_whenBookingNotFound_ThenReturnEmptyList() {
        List<Booking> result = bookingRepository.findAllByItem_OwnerAndStartAfter(user,
                LocalDateTime.now(), pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllByItem_OwnerAndStartAfter_whenBookingFound_ThenReturnList() {
        List<Booking> result = bookingRepository.findAllByItem_OwnerAndStartAfter(user,
                LocalDateTime.now().minusDays(10), pageable);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getBooker().getName(), user.getName());
        assertTrue(result.get(0).getStart().isAfter(LocalDateTime.now().minusDays(10)));
    }

    @Test
    public void findAllByItem_OwnerAndStatus_whenBookingNotFound_ThenReturnEmptyList() {
        List<Booking> result = bookingRepository.findAllByItem_OwnerAndStatus(user,
                BookingStatus.WAITING, pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllByItem_OwnerAndStatus_whenBookingFound_ThenReturnList() {
        List<Booking> result = bookingRepository.findAllByItem_OwnerAndStatus(user,
                BookingStatus.APPROVED, pageable);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getBooker().getName(), user.getName());
        assertEquals(result.get(0).getStatus(), BookingStatus.APPROVED);
    }

    @Test
    public void findFirstBookingByItemAndStartBeforeAndStatus_whenBookingNotFound_ThenReturnEmptyList() {
        Booking result = bookingRepository.findFirstBookingByItemAndStartBeforeAndStatus(item,
                LocalDateTime.now().plusDays(10), BookingStatus.WAITING, byId);

        assertNull(result);
    }

    @Test
    public void findFirstBookingByItemAndStartBeforeAndStatus_whenBookingFound_ThenReturnList() {
        Booking result = bookingRepository.findFirstBookingByItemAndStartBeforeAndStatus(item,
                LocalDateTime.now(), BookingStatus.APPROVED, byId);

        assertNotNull(result);
        assertEquals(result, booking);
        assertEquals(result.getStatus(), BookingStatus.APPROVED);

    }

    @Test
    public void findFirstBookingByItemAndStartAfterAndStatus_whenBookingNotFound_ThenReturnEmptyList() {
        Booking result = bookingRepository.findFirstBookingByItemAndStartAfterAndStatus(item,
                LocalDateTime.now(), BookingStatus.APPROVED, byId);

        assertNull(result);
    }

    @Test
    public void findFirstBookingByItemAndStartAfterAndStatus_whenBookingFound_ThenReturnList() {
        Booking result = bookingRepository.findFirstBookingByItemAndStartAfterAndStatus(item,
                LocalDateTime.now().minusDays(10), BookingStatus.APPROVED, byId);

        assertNotNull(result);
        assertEquals(result, booking);
        assertEquals(result.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    public void findAllByItemInAndStartBeforeAndStatus_whenBookingNotFound_ThenReturnEmptyList() {
        List<Booking> result = bookingRepository.findAllByItemInAndStartBeforeAndStatus(List.of(item),
                LocalDateTime.now().minusDays(10), BookingStatus.APPROVED, byId);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllByItemInAndStartBeforeAndStatus_whenBookingFound_ThenReturnList() {
        List<Booking> result = bookingRepository.findAllByItemInAndStartBeforeAndStatus(List.of(item),
                LocalDateTime.now(), BookingStatus.APPROVED, byId);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getItem().getName(), item.getName());
        assertEquals(result.get(0).getStatus(), BookingStatus.APPROVED);
    }

    @Test
    public void findAllByItemInAndStartAfterAndStatus_whenBookingNotFound_ThenReturnEmptyList() {
        List<Booking> result = bookingRepository.findAllByItemInAndStartAfterAndStatus(List.of(item),
                LocalDateTime.now(), BookingStatus.APPROVED, byId);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllByItemInAndStartAfterAndStatus_whenBookingFound_ThenReturnList() {
        List<Booking> result = bookingRepository.findAllByItemInAndStartAfterAndStatus(List.of(item),
                LocalDateTime.now().minusDays(10), BookingStatus.APPROVED, byId);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getBooker().getName(), user.getName());
    }

    @Test
    public void findFirstBookingByItemAndBookerAndStatusAndEndBefore_whenBookingNotFound_ThenReturnEmptyList() {
        Optional<Booking> result = bookingRepository.findFirstBookingByItemAndBookerAndStatusAndEndBefore(item,
                user, BookingStatus.APPROVED, LocalDateTime.now(), byId);

        assertFalse(result.isPresent());
    }

    @Test
    public void findFirstBookingByItemAndBookerAndStatusAndEndBefore_whenBookingFound_ThenReturnList() {
        Optional<Booking> result = bookingRepository.findFirstBookingByItemAndBookerAndStatusAndEndBefore(item,
                user, BookingStatus.APPROVED, LocalDateTime.now().plusDays(10), byId);

        assertTrue(result.isPresent());
        assertEquals(result.get().getBooker().getName(), user.getName());
    }

}
