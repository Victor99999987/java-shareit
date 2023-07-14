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
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    private MockMvc mvc;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();
    }

    @Test
    void findAll() throws Exception {
        List<UserDto> expectUsers = List.of(userDto);
        Mockito
                .when(userService.findAll())
                .thenReturn(expectUsers);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectUsers.size()))
                .andExpect(jsonPath("$[0].id").hasJsonPath())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").hasJsonPath())
                .andExpect(jsonPath("$[0].name").value("user"))
                .andExpect(jsonPath("$[0].email").hasJsonPath())
                .andExpect(jsonPath("$[0].email").value("user@user.com"))
                .andDo(print());

        Mockito.verify(userService, times(1)).findAll();
    }

    @Test
    void add() throws Exception {
        Mockito
                .when(userService.add(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").hasJsonPath())
                .andExpect(jsonPath("$.name").value("user"))
                .andExpect(jsonPath("$.email").hasJsonPath())
                .andExpect(jsonPath("$.email").value("user@user.com"))
                .andDo(print());

        Mockito.verify(userService, times(1)).add(any());
    }

    @Test
    void findById() throws Exception {
        Mockito
                .when(userService.findById(any()))
                .thenReturn(userDto);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").hasJsonPath())
                .andExpect(jsonPath("$.name").value("user"))
                .andExpect(jsonPath("$.email").hasJsonPath())
                .andExpect(jsonPath("$.email").value("user@user.com"))
                .andDo(print());

        Mockito.verify(userService, times(1)).findById(any());
    }

    @Test
    void remove() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andDo(print());

        Mockito.verify(userService, times(1)).remove(any());
    }

    @Test
    void update() throws Exception {
        Mockito
                .when(userService.update(anyLong(), any()))
                .thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").hasJsonPath())
                .andExpect(jsonPath("$.name").value("user"))
                .andExpect(jsonPath("$.email").hasJsonPath())
                .andExpect(jsonPath("$.email").value("user@user.com"))
                .andDo(print());

        Mockito.verify(userService, times(1)).update(anyLong(), any());
    }

}
