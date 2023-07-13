package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingValidationException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.common.ErrorHandler;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemValidationException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private MockMvc mvc;

    private User user;

    private Item item;

    private List<Item> itemList;

    private ItemDto itemDto;

    private List<ItemDto> itemDtoList;

    private Booking booking;

    private BookingDtoIn bookingDtoIn;
    private List<Booking> bookingList;

    private Comment comment;

    private List<Comment> commentList;

    private CommentDto commentDto;
    private Request request;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingService)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("простая вещь")
                .available(true)
                .description("простая вещь")
                .owner(user)
                .build();

        itemList = List.of(item);

        itemDto = ItemMapper.toDto(item);

        itemDtoList = List.of(itemDto);

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();

        bookingList = List.of(booking);

        comment = Comment.builder()
                .id(1L)
                .text("комментарий")
                .item(item)
                .author(user)
                .build();

        commentList = List.of(comment);

        commentDto = CommentMapper.toDto(comment);

        request = Request.builder()
                .id(1L)
                .description("нужна ложка, меду покушать")
                .requestor(user)
                .build();
    }

    @Test
    void add_whenUserNotFound_thenException() throws Exception {
        Long userId = 1L;
        Long id = 1L;
        bookingDtoIn = new BookingDtoIn();

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(
                UserNotFoundException.class, () -> bookingService.add(userId, bookingDtoIn));
    }

    @Test
    void add_whenItemNotFound_thenException() throws Exception {
        Long userId = 1L;
        Long id = 1L;
        bookingDtoIn = new BookingDtoIn();
        bookingDtoIn.setItemId(1L);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ItemNotFoundException itemNotFoundException = assertThrows(
                ItemNotFoundException.class, () -> bookingService.add(userId, bookingDtoIn));
    }

    @Test
    void add_whenItemNotAvailable_thenException() throws Exception {
        Long userId = 1L;
        Long id = 1L;
        bookingDtoIn = new BookingDtoIn();
        bookingDtoIn.setItemId(1L);
        item.setAvailable(false);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ItemValidationException itemValidationException = assertThrows(
                ItemValidationException.class, () -> bookingService.add(userId, bookingDtoIn));
    }

    @Test
    void add_whenUserIsOwner_thenException() throws Exception {
        Long userId = 1L;
        Long id = 1L;
        bookingDtoIn = new BookingDtoIn();
        bookingDtoIn.setItemId(1L);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ItemNotFoundException itemNotFoundException = assertThrows(
                ItemNotFoundException.class, () -> bookingService.add(userId, bookingDtoIn));
    }

    @Test
    void add_whenStartAfterEnd_thenException() throws Exception {
        Long userId = 1L;
        Long id = 1L;
        bookingDtoIn = new BookingDtoIn();
        bookingDtoIn.setItemId(1L);
        bookingDtoIn.setStart(LocalDateTime.now().plusDays(3));
        bookingDtoIn.setEnd(LocalDateTime.now().plusDays(2));
        item.setOwner(new User(3L, "", ""));

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        BookingValidationException bookingValidationException = assertThrows(
                BookingValidationException.class, () -> bookingService.add(userId, bookingDtoIn));
    }

    @Test
    void add_whenStartEqualsEnd_thenException() throws Exception {
        Long userId = 1L;
        Long id = 1L;
        bookingDtoIn = new BookingDtoIn();
        bookingDtoIn.setItemId(1L);
        LocalDateTime curTime= LocalDateTime.now();
        bookingDtoIn.setStart(curTime.plusDays(3));
        bookingDtoIn.setEnd(curTime.plusDays(3));
        item.setOwner(new User(3L, "", ""));

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        BookingValidationException bookingValidationException = assertThrows(
                BookingValidationException.class, () -> bookingService.add(userId, bookingDtoIn));
    }

    @Test
    void add_whenParamIsOk_thenReturn() throws Exception {
        Long userId = 1L;
        Long id = 1L;
        bookingDtoIn = new BookingDtoIn();
        bookingDtoIn.setItemId(1L);
        bookingDtoIn.setStart(LocalDateTime.now().plusDays(3));
        bookingDtoIn.setEnd(LocalDateTime.now().plusDays(4));
        item.setOwner(new User(3L, "", ""));

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingDtoOut result = bookingService.add(userId, bookingDtoIn);

        assertEquals(result.getId(), itemDto.getId());
    }

    @Test
    void update_whenUserNotFound_thenException() throws Exception {
        Long userId = 1L;
        Long id = 1L;
        boolean approved = true;

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(
                UserNotFoundException.class, () -> bookingService.update(userId, id, approved));
    }

    @Test
    void update_whenBookingNotFound_thenException() throws Exception {
        Long userId = 1L;
        Long id = 1L;
        boolean approved = true;

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        BookingNotFoundException bookingNotFoundException = assertThrows(
                BookingNotFoundException.class, () -> bookingService.update(userId, id, approved));
    }

    @Test
    void update_whenUserNotOwner_thenException() throws Exception {
        Long userId = 1L;
        Long id = 1L;
        boolean approved = true;
        item.setOwner(new User(3L, "", ""));

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingNotFoundException bookingNotFoundException = assertThrows(
                BookingNotFoundException.class, () -> bookingService.update(userId, id, approved));
    }

    @Test
    void update_whenDoubleUpdate_thenException() throws Exception {
        Long userId = 1L;
        Long id = 1L;
        boolean approved = true;
        booking.setStatus(BookingStatus.WAITING);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Mockito
                .when(bookingRepository.save(any()))
                .thenReturn(booking);

        bookingService.update(userId, id, approved);

        BookingValidationException bookingValidationException = assertThrows(
                BookingValidationException.class, () -> bookingService.update(userId, id, approved));

        Mockito.verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void update_whenUserIsOwner_thenReturn() throws Exception {
        Long userId = 1L;
        Long id = 1L;
        boolean approved = true;
        booking.setStatus(BookingStatus.WAITING);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Mockito
                .when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingDtoOut result = bookingService.update(userId, id, approved);

        assertEquals(result.getId(), booking.getId());
        assertNotNull(result.getItem());
    }

    @Test
    void findById_whenUserNotFound_thenException() throws Exception {
        Long userId = 1L;
        Long id = 1L;

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(
                UserNotFoundException.class, () -> bookingService.findById(userId, id));
    }

    @Test
    void findById_whenBookingNotFound_thenException() throws Exception {
        Long userId = 1L;
        Long id = 1L;

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        BookingNotFoundException bookingNotFoundException = assertThrows(
                BookingNotFoundException.class, () -> bookingService.findById(userId, id));
    }

    @Test
    void findById_whenUserIsNotBooker_thenException() throws Exception {
        Long userId = 1L;
        Long id = 1L;
        booking.setBooker(new User(3L, "", ""));
        item.setOwner(new User(5L, "", ""));

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingNotFoundException bookingNotFoundException = assertThrows(
                BookingNotFoundException.class, () -> bookingService.findById(userId, id));
    }

    @Test
    void findById_whenUserIsBooker_thenReturn() throws Exception {
        Long userId = 1L;
        Long id = 1L;
        item.setOwner(new User(5L, "", ""));

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDtoOut result = bookingService.findById(userId, id);

        assertEquals(result.getId(), booking.getId());
        assertNotNull(result.getItem());
    }

    @Test
    void findAllByUserId_whenUserNotFound_thenException() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        String stateIn = "ALL";

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(
                UserNotFoundException.class, () -> bookingService.findAllByUserId(userId, stateIn, from, size));
    }

    @Test
    void findAllByUserId_whenUnknownStatus_thenException() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        String stateIn = "badState";

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        BookingValidationException bookingValidationException = assertThrows(
                BookingValidationException.class, () -> bookingService.findAllByUserId(userId, stateIn, from, size));
    }

    @Test
    void findAllByUserId_whenStatusAll_thenReturn() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        String stateIn = "ALL";

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findAllByBooker(any(), any()))
                .thenReturn(bookingList);

        List<BookingDtoOut> result = bookingService.findAllByUserId(userId, stateIn, from, size);

        assertEquals(result.size(), bookingList.size());
    }

    @Test
    void findAllByOwnerId_whenUserNotFound_thenException() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        String stateIn = "ALL";

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(
                UserNotFoundException.class, () -> bookingService.findAllByOwnerId(userId, stateIn, from, size));
    }

    @Test
    void findAllByOwnerId_whenUnknownStatus_thenException() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        String stateIn = "badState";

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        BookingValidationException bookingValidationException = assertThrows(
                BookingValidationException.class, () -> bookingService.findAllByOwnerId(userId, stateIn, from, size));
    }

    @Test
    void findAllByOwnerId_whenStatusAll_thenReturn() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        String stateIn = "ALL";

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findAllByItem_Owner(any(), any()))
                .thenReturn(bookingList);

        List<BookingDtoOut> result = bookingService.findAllByOwnerId(userId, stateIn, from, size);

        assertEquals(result.size(), bookingList.size());
    }

    @Test
    void findAllByOwnerId_whenStatusCURRENT_thenReturn() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        String stateIn = "CURRENT";

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findAllByItem_OwnerAndStartBeforeAndEndAfter(any(), any(), any(), any()))
                .thenReturn(bookingList);

        List<BookingDtoOut> result = bookingService.findAllByOwnerId(userId, stateIn, from, size);

        assertEquals(result.size(), bookingList.size());
    }

    @Test
    void findAllByOwnerId_whenStatusPAST_thenReturn() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        String stateIn = "PAST";

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findAllByItem_OwnerAndEndBefore(any(), any(), any()))
                .thenReturn(bookingList);

        List<BookingDtoOut> result = bookingService.findAllByOwnerId(userId, stateIn, from, size);

        assertEquals(result.size(), bookingList.size());
    }

    @Test
    void findAllByOwnerId_whenStatusFUTURE_thenReturn() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        String stateIn = "FUTURE";

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findAllByItem_OwnerAndStartAfter(any(), any(), any()))
                .thenReturn(bookingList);

        List<BookingDtoOut> result = bookingService.findAllByOwnerId(userId, stateIn, from, size);

        assertEquals(result.size(), bookingList.size());
    }

    @Test
    void findAllByOwnerId_whenStatusWAITING_thenReturn() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        String stateIn = "WAITING";

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findAllByItem_OwnerAndStatus(any(), any(), any()))
                .thenReturn(bookingList);

        List<BookingDtoOut> result = bookingService.findAllByOwnerId(userId, stateIn, from, size);

        assertEquals(result.size(), bookingList.size());
    }

    @Test
    void findAllByOwnerId_whenStatusREJECTED_thenReturn() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        String stateIn = "REJECTED";

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findAllByItem_OwnerAndStatus(any(), any(), any()))
                .thenReturn(bookingList);

        List<BookingDtoOut> result = bookingService.findAllByOwnerId(userId, stateIn, from, size);

        assertEquals(result.size(), bookingList.size());
    }

}
