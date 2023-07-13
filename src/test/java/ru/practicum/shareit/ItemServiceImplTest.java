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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.ErrorHandler;
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
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.validateMockitoUsage;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private User user;

    private Item item;

    private List<Item> itemList;

    private ItemDto itemDto;

    private List<ItemDto> itemDtoList;

    private Booking booking;

    private List<Booking> bookingList;

    private Comment comment;

    private List<Comment> commentList;

    private CommentDto commentDto;
    private Request request;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemService)
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
    void findAllByUserId_whenUserNotFount_thenException() throws Exception{
        Long id = 1L;
        int from = 0;
        int size = 10;
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(
                UserNotFoundException.class, () -> itemService.findAllByUserId(id,from,size));
    }

    @Test
    void findAllByUserId_whenFromLess0_thenException() throws Exception{
        Long id = 1L;
        int from = -1;
        int size = 10;
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        ItemValidationException itemValidationException = assertThrows(
                ItemValidationException.class, () -> itemService.findAllByUserId(id,from,size));
    }

    @Test
    void findAllByUserId_whenSizeLess0_thenException() throws Exception{
        Long id = 1L;
        int from = 0;
        int size = -1;
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        ItemValidationException itemValidationException = assertThrows(
                ItemValidationException.class, () -> itemService.findAllByUserId(id,from,size));
    }

    @Test
    void findAllByUserId_whenUserFound_thenReturnList() throws Exception{
        Long id = 1L;
        int from = 0;
        int size = 10;
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.findAllByOwner(any(),any()))
                .thenReturn(itemList);

        Mockito
                .when(bookingRepository.findAllByItemInAndStartBeforeAndStatus(any(),any(),any(),any()))
                .thenReturn(bookingList);

        Mockito
                .when(bookingRepository.findAllByItemInAndStartAfterAndStatus(any(),any(),any(),any()))
                .thenReturn(bookingList);

        Mockito
                .when(commentRepository.findAllByItemIn(any(),any()))
                .thenReturn(commentList);

        List<ItemDto> result = itemService.findAllByUserId(id,from,size);

        assertEquals(itemList.size(), result.size());
    }

    @Test
    void findById_whenUserNotFound_thenException() throws Exception{
        Long userId = 1L;
        Long id = 1L;
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(
                UserNotFoundException.class, () -> itemService.findById(userId,id));
    }

    @Test
    void findById_whenItemNotFound_thenException() throws Exception{
        Long userId = 1L;
        Long id = 1L;
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ItemNotFoundException itemNotFoundException = assertThrows(
                ItemNotFoundException.class, () -> itemService.findById(userId,id));
    }

    @Test
    void findById_whenItemFound_thenReturn() throws Exception{
        Long userId = 1L;
        Long id = 1L;
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(bookingRepository.findFirstBookingByItemAndStartBeforeAndStatus(any(),any(),any(),any()))
                .thenReturn(booking);

        Mockito
                .when(bookingRepository.findFirstBookingByItemAndStartAfterAndStatus(any(),any(),any(),any()))
                .thenReturn(booking);

        Mockito
                .when(commentRepository.findAllByItem(any(),any()))
                .thenReturn(commentList);

        ItemDto result = itemService.findById(userId,id);

        assertEquals(result.getId(), itemDto.getId());
        assertEquals(result.getName(), itemDto.getName());
        assertEquals(result.getDescription(), itemDto.getDescription());
    }

    @Test
    void add_whenUserNotFound_thenException() throws Exception{
        Long userId = 1L;
        Long id = 1L;
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(
                UserNotFoundException.class, () -> itemService.add(userId,itemDto));
    }

    @Test
    void add_whenUserFoundAndRequestIsNull_thenReturn() throws Exception{
        Long userId = 1L;
        Long id = 1L;
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto result = itemService.add(userId,itemDto);

        assertEquals(result.getId(), itemDto.getId());
        assertEquals(result.getName(), itemDto.getName());
        assertEquals(result.getDescription(), itemDto.getDescription());
    }

    @Test
    void add_whenUserFoundAndRequestNotFound_thenException() throws Exception{
        Long userId = 1L;
        Long id = 1L;
        itemDto.setRequestId(99999L);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        RequestNotFoundException requestNotFoundException = assertThrows(
                RequestNotFoundException.class, () -> itemService.add(userId,itemDto));
    }

    @Test
    void add_whenUserFoundAndRequestFound_thenReturn() throws Exception{
        Long userId = 1L;
        Long id = 1L;

        itemDto.setRequestId(1L);
        item = ItemMapper.toItem(itemDto);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));

        Mockito
                .when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto result = itemService.add(userId,itemDto);

        assertEquals(result.getId(), itemDto.getId());
        assertEquals(result.getName(), itemDto.getName());
        assertEquals(result.getDescription(), itemDto.getDescription());
        assertEquals(result.getRequestId(), itemDto.getRequestId());
    }

    @Test
    void remove_whenUserNotFound_thenException() throws Exception{
        Long userId = 1L;
        Long id = 1L;
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(
                UserNotFoundException.class, () -> itemService.remove(userId,id));
    }

    @Test
    void remove_whenUserFoundAndItemNotFound_thenException() throws Exception{
        Long userId = 1L;
        Long id = 1L;

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ItemNotFoundException itemNotFoundException = assertThrows(
                ItemNotFoundException.class, () -> itemService.remove(userId,id));
    }

    @Test
    void remove_whenUserNotOwner_thenException() throws Exception{
        Long userId = 1L;
        Long id = 1L;

        item.setOwner(new User(5L, "newOwner", ""));

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ItemNotFoundException itemNotFoundException = assertThrows(
                ItemNotFoundException.class, () -> itemService.remove(userId,id));
    }

    @Test
    void remove_whenUserIsOwner_thenDelete() throws Exception{
        Long userId = 1L;
        Long id = 1L;

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        itemService.remove(userId, id);
        Mockito.verify(itemRepository, times(1)).deleteById(id);
    }

    @Test
    void update_whenUserNotFound_thenException() throws Exception{
        Long userId = 1L;
        Long id = 1L;
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(
                UserNotFoundException.class, () -> itemService.update(userId,id,itemDto));
    }

    @Test
    void update_whenUserFoundAndItemNotFound_thenException() throws Exception{
        Long userId = 1L;
        Long id = 1L;

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ItemNotFoundException itemNotFoundException = assertThrows(
                ItemNotFoundException.class, () -> itemService.update(userId,id,itemDto));
    }

    @Test
    void update_whenUserFoundAndRequestFound_thenReturn() throws Exception{
        Long userId = 1L;
        Long id = 1L;

        itemDto.setRequestId(1L);
        item = ItemMapper.toItem(itemDto);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto result = itemService.update(userId,id, itemDto);

        assertEquals(result.getId(), itemDto.getId());
        assertEquals(result.getName(), itemDto.getName());
        assertEquals(result.getDescription(), itemDto.getDescription());
    }

    @Test
    void search_whenTextEmpty_thenReturnEmptyList() throws Exception{
        String text = "";
        int from = 0;
        int size = 10;

        List<ItemDto> result = itemService.search(text, from, size);

        assertTrue(result.isEmpty());
    }

    @Test
    void search_whenFromLess0_thenException() throws Exception{
        String text = "text";
        int from = -1;
        int size = 10;

        ItemValidationException itemValidationException = assertThrows(
                ItemValidationException.class, () -> itemService.search(text,from,size));

    }

    @Test
    void search_whenSizeIs0_thenException() throws Exception{
        String text = "text";
        int from = 0;
        int size = 0;

        ItemValidationException itemValidationException = assertThrows(
                ItemValidationException.class, () -> itemService.search(text,from,size));

    }

    @Test
    void search_whenParamIsOk_thenReturn() throws Exception{
        String text = "text";
        int from = 0;
        int size = 10;

        Mockito
                .when(itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(anyString(),anyString(),anyBoolean(),any()))
                .thenReturn(itemList);

        List<ItemDto> result = itemService.search(text,from,size);

        assertEquals(itemList.size(), result.size());
    }

    @Test
    void addComment_whenUserNotFound_thenException() throws Exception{
        Long userId = 1L;
        Long id = 1L;
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(
                UserNotFoundException.class, () -> itemService.addComment(userId,id, CommentMapper.toDto(comment)));
    }

    @Test
    void addComment_whenItemNotFound_thenException() throws Exception{
        Long userId = 1L;
        Long id = 1L;

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ItemNotFoundException itemNotFoundException = assertThrows(
                ItemNotFoundException.class, () -> itemService.addComment(userId,id, CommentMapper.toDto(comment)));
    }

    @Test
    void addComment_whenBookingIsRuning_thenException() throws Exception{
        Long userId = 1L;
        Long id = 1L;

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(bookingRepository.findFirstBookingByItemAndBookerAndStatusAndEndBefore(any(),any(),any(),any(),any()))
                .thenReturn(Optional.empty());

        ItemValidationException itemValidationException = assertThrows(
                ItemValidationException.class, () -> itemService.addComment(userId,id, CommentMapper.toDto(comment)));
    }

    @Test
    void addComment_whenParamIsOk_thenReturn() throws Exception{
        Long userId = 1L;
        Long id = 1L;

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(bookingRepository.findFirstBookingByItemAndBookerAndStatusAndEndBefore(any(),any(),any(),any(),any()))
                .thenReturn(Optional.of(booking));

        Mockito
                .when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDto result = itemService.addComment(userId,id, commentDto);

        assertEquals(result.getText(), commentDto.getText());
    }

}
