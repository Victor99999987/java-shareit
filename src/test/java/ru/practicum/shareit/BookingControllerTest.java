package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.common.ErrorHandler;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private BookingDtoIn bookingDtoIn;
    private BookingDtoOut bookingDtoOut;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        mapper.registerModule(new JavaTimeModule());

        User booker = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

        User owner = User.builder()
                .id(2L)
                .name("owner")
                .email("owner@user.com")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Молоток")
                .description("Забивает гвоздь за удар")
                .available(true)
                .owner(owner)
                .build();

        LocalDateTime curTime = LocalDateTime.now();

        bookingDtoIn = BookingDtoIn.builder()
                .itemId(1L)
                .start(curTime.plusDays(2))
                .end(curTime.plusDays(5))
                .build();

        bookingDtoOut = BookingDtoOut.builder()
                .id(1L)
                .start(curTime.plusDays(2))
                .end(curTime.plusDays(5))
                .status(BookingStatus.WAITING)
                .booker(booker)
                .item(item)
                .build();
    }

    @Test
    void add() throws Exception {
        Mockito
                .when(bookingService.add(anyLong(), any()))
                .thenReturn(bookingDtoOut);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.id").value(bookingDtoOut.getId()))
                .andExpect(jsonPath("$.start").hasJsonPath())
                .andExpect(jsonPath("$.end").hasJsonPath())
                .andExpect(jsonPath("$.status").hasJsonPath())
                .andExpect(jsonPath("$.status").value(bookingDtoOut.getStatus().toString()))
                .andDo(print());

        Mockito.verify(bookingService, times(1)).add(anyLong(), any());
    }

    @Test
    void update() throws Exception {
        Mockito
                .when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDtoOut);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.id").value(bookingDtoOut.getId()))
                .andExpect(jsonPath("$.start").hasJsonPath())
                .andExpect(jsonPath("$.end").hasJsonPath())
                .andExpect(jsonPath("$.status").hasJsonPath())
                .andExpect(jsonPath("$.status").value(bookingDtoOut.getStatus().toString()))
                .andDo(print());

        Mockito.verify(bookingService, times(1)).update(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void findById() throws Exception {
        Mockito
                .when(bookingService.findById(anyLong(), anyLong()))
                .thenReturn(bookingDtoOut);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.id").value(bookingDtoOut.getId()))
                .andExpect(jsonPath("$.start").hasJsonPath())
                .andExpect(jsonPath("$.end").hasJsonPath())
                .andExpect(jsonPath("$.status").hasJsonPath())
                .andExpect(jsonPath("$.status").value(bookingDtoOut.getStatus().toString()))
                .andDo(print());

        Mockito.verify(bookingService, times(1)).findById(anyLong(), anyLong());
    }

    @Test
    void findAllByUserId() throws Exception {
        List<BookingDtoOut> expectedDtos = List.of(bookingDtoOut);
        Mockito
                .when(bookingService.findAllByUserId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(expectedDtos);

        mvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedDtos.size()))
                .andExpect(jsonPath("$[0].id").hasJsonPath())
                .andExpect(jsonPath("$[0].id").value(bookingDtoOut.getId()))
                .andExpect(jsonPath("$[0].start").hasJsonPath())
                .andExpect(jsonPath("$[0].end").hasJsonPath())
                .andExpect(jsonPath("$[0].status").hasJsonPath())
                .andExpect(jsonPath("$[0].status").value(bookingDtoOut.getStatus().toString()))
                .andDo(print());

        Mockito.verify(bookingService, times(1)).findAllByUserId(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void findAllByOwnerId() throws Exception {
        List<BookingDtoOut> expectedDtos = List.of(bookingDtoOut);
        Mockito
                .when(bookingService.findAllByOwnerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(expectedDtos);

        mvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedDtos.size()))
                .andExpect(jsonPath("$[0].id").hasJsonPath())
                .andExpect(jsonPath("$[0].id").value(bookingDtoOut.getId()))
                .andExpect(jsonPath("$[0].start").hasJsonPath())
                .andExpect(jsonPath("$[0].end").hasJsonPath())
                .andExpect(jsonPath("$[0].status").hasJsonPath())
                .andExpect(jsonPath("$[0].status").value(bookingDtoOut.getStatus().toString()))
                .andDo(print());

        Mockito.verify(bookingService, times(1)).findAllByOwnerId(anyLong(), anyString(), anyInt(), anyInt());
    }

}
