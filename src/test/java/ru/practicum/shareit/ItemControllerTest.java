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
import ru.practicum.shareit.common.ErrorHandler;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private ItemDto itemDto;
    private ItemDto updateItemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Рулетка")
                .description("Хорошая, только начинается с 50 сантиметров")
                .available(true)
                .build();

        updateItemDto = ItemDto.builder()
                .id(1L)
                .name("Рулетка")
                .description("Еще полметра отломали")
                .available(true)
                .build();

        commentDto = CommentDto.builder()
                .text("На помойку лучше выкиньте эту вашу рулетку")
                .build();
    }

    @Test
    void findAllByUserId() throws Exception {
        List<ItemDto> expectItems = List.of(itemDto);
        Mockito
                .when(itemService.findAllByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(expectItems);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectItems.size()))
                .andExpect(jsonPath("$[0].id").hasJsonPath())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").hasJsonPath())
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").hasJsonPath())
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").hasJsonPath())
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()))
                .andDo(print());

        Mockito.verify(itemService, times(1)).findAllByUserId(anyLong(), anyInt(), anyInt());
    }

    @Test
    void finById() throws Exception {
        Mockito
                .when(itemService.findById(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").hasJsonPath())
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").hasJsonPath())
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").hasJsonPath())
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andDo(print());

        Mockito.verify(itemService, times(1)).findById(anyLong(), anyLong());
    }

    @Test
    void add() throws Exception {
        Mockito
                .when(itemService.add(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").hasJsonPath())
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").hasJsonPath())
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").hasJsonPath())
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andDo(print());

        Mockito.verify(itemService, times(1)).add(anyLong(), any());
    }

    @Test
    void remove() throws Exception {
        mvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andDo(print());

        Mockito.verify(itemService, times(1)).remove(anyLong(), anyLong());
    }

    @Test
    void update() throws Exception {
        Mockito
                .when(itemService.update(anyLong(), anyLong(), any()))
                .thenReturn(updateItemDto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.id").value(updateItemDto.getId()))
                .andExpect(jsonPath("$.name").hasJsonPath())
                .andExpect(jsonPath("$.name").value(updateItemDto.getName()))
                .andExpect(jsonPath("$.description").hasJsonPath())
                .andExpect(jsonPath("$.description").value(updateItemDto.getDescription()))
                .andExpect(jsonPath("$.available").hasJsonPath())
                .andExpect(jsonPath("$.available").value(updateItemDto.getAvailable()))
                .andDo(print());

        Mockito.verify(itemService, times(1)).update(anyLong(), anyLong(), any());
    }

    @Test
    void search() throws Exception {
        List<ItemDto> expectItems = List.of(itemDto);
        Mockito
                .when(itemService.search(anyString(), anyInt(), anyInt()))
                .thenReturn(expectItems);

        mvc.perform(get("/items/search")
                        .param("text", "рулетка")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectItems.size()))
                .andExpect(jsonPath("$[0].id").hasJsonPath())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").hasJsonPath())
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").hasJsonPath())
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").hasJsonPath())
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()))
                .andDo(print());

        Mockito.verify(itemService, times(1)).search(anyString(), anyInt(), anyInt());
    }

    @Test
    void addComment() throws Exception {
        Mockito
                .when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenThrow(new UserNotFoundException("Пользователь не существует"));

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "9999")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

        Mockito.verify(itemService, times(1)).addComment(anyLong(), anyLong(), any());
    }

}
