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
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.exception.RequestValidationException;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.impl.RequestServiceImpl;
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
public class RequestServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    private final ObjectMapper mapper = new ObjectMapper();

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
    private RequestDto requestDto;
    private List<Request> requestList;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(requestService)
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
        requestDto = RequestMapper.toDto(request);
        requestList = List.of(request);
    }

    @Test
    void add_whenUserNotFound_thenException() throws Exception{
        Long userId = 1L;

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(
                UserNotFoundException.class, () -> requestService.add(userId,requestDto));
    }

    @Test
    void add_whenUserFound_thenReturn() throws Exception{
        Long userId = 1L;

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(requestRepository.save(any()))
                .thenReturn(request);

        RequestDto result = requestService.add(userId,requestDto);

        assertEquals(result.getId(), requestDto.getId());
        assertEquals(result.getDescription(), requestDto.getDescription());
    }

    @Test
    void findAllByUserId_whenUserNotFound_thenException() throws Exception{
        Long userId = 1L;

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(
                UserNotFoundException.class, () -> requestService.findAllByUserId(userId));
    }

    @Test
    void findAllByUserId_whenUserFound_thenReturn() throws Exception{
        Long userId = 1L;
        item.setRequest(request);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(requestRepository.findAllByRequestor(any(), any()))
                .thenReturn(requestList);

        Mockito
                .when(itemRepository.findAllByRequestIn(any(), any()))
                .thenReturn(itemList);

        List<RequestDto> result = requestService.findAllByUserId(userId);

        assertEquals(result.size(), requestList.size());
    }

    @Test
    void findAll_whenUserNotFound_thenException() throws Exception{
        Long userId = 1L;
        int from=0;
        int size=10;

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(
                UserNotFoundException.class, () -> requestService.findAll(userId,from,size));
    }

    @Test
    void findAll_whenFromLess0_thenException() throws Exception{
        Long userId = 1L;
        int from=-1;
        int size=10;

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        RequestValidationException requestValidationException = assertThrows(
                RequestValidationException.class, () -> requestService.findAll(userId,from,size));
    }

    @Test
    void findAll_whenSizeLess0_thenException() throws Exception{
        Long userId = 1L;
        int from=0;
        int size=-1;

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        RequestValidationException requestValidationException = assertThrows(
                RequestValidationException.class, () -> requestService.findAll(userId,from,size));
    }

    @Test
    void findAll_whenUserFound_thenReturn() throws Exception{
        Long userId = 1L;
        int from=0;
        int size=10;
        item.setRequest(request);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(requestRepository.findAllByRequestorNot(any(),any()))
                .thenReturn(requestList);

        Mockito
                .when(itemRepository.findAllByRequestIn(any(),any()))
                .thenReturn(itemList);

        List<RequestDto> result = requestService.findAll(userId, from, size);

        assertEquals(result.size(), requestList.size());
    }

    @Test
    void findById_whenUserNotFound_thenException() throws Exception{
        Long userId = 1L;
        Long id = 1L;

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(
                UserNotFoundException.class, () -> requestService.findById(userId,id));
    }

    @Test
    void findById_whenRequestNotFound_thenException() throws Exception{
        Long userId = 1L;
        Long id = 1L;

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        RequestNotFoundException requestNotFoundException = assertThrows(
                RequestNotFoundException.class, () -> requestService.findById(userId,id));
    }

    @Test
    void findById_whenFound_thenReturn() throws Exception{
        Long userId = 1L;
        Long id = 1L;

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));

        Mockito
                .when(itemRepository.findAllByRequest(any(),any()))
                .thenReturn(itemList);

        RequestDto result = requestService.findById(userId, id);

        assertEquals(result.getId(), request.getId());
    }

}
