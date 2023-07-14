package ru.practicum.shareit.service;

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
import ru.practicum.shareit.common.ErrorHandler;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private MockMvc mvc;

    private User user;
    private List<User> userList;

    private UserDto userDto;
    private List<UserDto> userDtoList;


    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(userService)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

        userList = List.of(user);

        userDto = UserMapper.toDto(user);

        userDtoList = List.of(userDto);
    }

    @Test
    void findAll() throws Exception {
        Mockito
                .when(userRepository.findAll())
                .thenReturn(userList);

        List<UserDto> result = userService.findAll();

        assertEquals(userDtoList.size(), result.size());
    }

    @Test
    void findById_whenUserFound_thenReturnUser() throws Exception {
        Long id = 1L;
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        UserDto result = userService.findById(id);

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
    }

    @Test
    void findById_whenUserNotFound_thenException() throws Exception {
        Long id = 1L;
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(
                UserNotFoundException.class, () -> userService.findById(id));
    }

    @Test
    void add_whenUserOk_thenReturnUser() throws Exception {
        Mockito
                .when(userRepository.save(any()))
                .thenReturn(user);

        UserDto result = userService.add(userDto);

        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    void remove_whenUserFound_thenDelete() throws Exception {
        Long id = 1L;
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        userService.remove(id);
        Mockito.verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    void remove_whenUserNotFound_thenException() throws Exception {
        Long id = 1L;
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(
                UserNotFoundException.class, () -> userService.findById(id));
        Mockito.verify(userRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void update_whenUserNotFound_thenException() throws Exception {
        Long id = 1L;
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(
                UserNotFoundException.class, () -> userService.update(id, userDto));
    }

    @Test
    void update_whenUserFound_thenReturnUser() throws Exception {
        Long id = 1L;

        User updateUser = User.builder()
                .id(1L)
                .name("newName")
                .email("newemail@mail.ru")
                .build();
        UserDto upadteUserDto = UserMapper.toDto(updateUser);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(userRepository.save(any()))
                .thenReturn(updateUser);

        UserDto result = userService.update(id, upadteUserDto);

        assertEquals(upadteUserDto.getId(), result.getId());
        assertEquals(upadteUserDto.getName(), result.getName());
        assertEquals(upadteUserDto.getEmail(), result.getEmail());
    }

}
