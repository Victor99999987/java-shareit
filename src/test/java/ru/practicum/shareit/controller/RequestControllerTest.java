package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RequestControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private RequestService requestService;
    @InjectMocks
    private RequestController requestController;
    private MockMvc mvc;

    private RequestDto requestDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(requestController)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        requestDto = RequestDto.builder()
                .id(1L)
                .description("Нужны ежовые руковицы для ловли ежа")
                .build();
    }

    @Test
    void add() throws Exception {
        Mockito
                .when(requestService.add(anyLong(), any()))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").hasJsonPath())
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.created").hasJsonPath())
                .andExpect(jsonPath("$.items").hasJsonPath())
                .andExpect(jsonPath("$.items").isEmpty())
                .andDo(print());

        Mockito.verify(requestService, times(1)).add(anyLong(), any());
    }

    @Test
    void findAllByUserId() throws Exception {
        List<RequestDto> expectedDtos = List.of(requestDto);
        Mockito
                .when(requestService.findAllByUserId(anyLong()))
                .thenReturn(expectedDtos);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedDtos.size()))
                .andExpect(jsonPath("$[0].id").hasJsonPath())
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()))
                .andExpect(jsonPath("$[0].description").hasJsonPath())
                .andExpect(jsonPath("$[0].description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$[0].created").hasJsonPath())
                .andExpect(jsonPath("$[0].items").hasJsonPath())
                .andExpect(jsonPath("$[0].items").isEmpty())
                .andDo(print());

        Mockito.verify(requestService, times(1)).findAllByUserId(anyLong());
    }

    @Test
    void findAll() throws Exception {
        List<RequestDto> expectedDtos = List.of(requestDto);
        Mockito
                .when(requestService.findAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(expectedDtos);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedDtos.size()))
                .andExpect(jsonPath("$[0].id").hasJsonPath())
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()))
                .andExpect(jsonPath("$[0].description").hasJsonPath())
                .andExpect(jsonPath("$[0].description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$[0].created").hasJsonPath())
                .andExpect(jsonPath("$[0].items").hasJsonPath())
                .andExpect(jsonPath("$[0].items").isEmpty())
                .andDo(print());

        Mockito.verify(requestService, times(1)).findAll(anyLong(), anyInt(), anyInt());
    }

    @Test
    void findById() throws Exception {
        Mockito
                .when(requestService.findById(anyLong(), anyLong()))
                .thenReturn(requestDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").hasJsonPath())
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.created").hasJsonPath())
                .andExpect(jsonPath("$.items").hasJsonPath())
                .andExpect(jsonPath("$.items").isEmpty())
                .andDo(print());

        Mockito.verify(requestService, times(1)).findById(anyLong(), anyLong());
    }

}
