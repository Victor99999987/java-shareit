package ru.practicum.shareit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class RequestRepositoryTest {

    private final Sort byId = Sort.by(Sort.Direction.ASC, "id");
    private final Pageable pageable = PageRequest.of(0, 10, byId);
    @Autowired
    UserRepository userRepository;
    @Autowired
    RequestRepository requestRepository;
    private User user;
    private User user2;
    private User user3;
    private Request request;
    private Request request2;

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
        user3 = User.builder()
                .name("user3")
                .email("user3@user3.com")
                .build();
        request = Request.builder()
                .requestor(user)
                .created(LocalDateTime.now())
                .description("нужен скотч склеить книгу")
                .build();
        request2 = Request.builder()
                .requestor(user2)
                .created(LocalDateTime.now())
                .description("нужен гаечный глюч на 88")
                .build();

        user = userRepository.save(user);
        user2 = userRepository.save(user2);
        user3 = userRepository.save(user3);

        request = requestRepository.save(request);
        request2 = requestRepository.save(request2);
    }

    @Test
    public void findAllByRequestor_whenRequestNotFound_ThenReturnEmptyList() {
        List<Request> result = requestRepository.findAllByRequestor(user3, byId);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllByRequestor_whenRequestFound_ThenReturnList() {
        List<Request> result = requestRepository.findAllByRequestor(user, byId);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getRequestor().getName(), user.getName());
    }

    @Test
    public void findAllByRequestorNot_whenRequestFound_ThenReturnList() {
        List<Request> result = requestRepository.findAllByRequestorNot(user3, pageable);

        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getRequestor().getName(), user.getName());
        assertEquals(result.get(1).getRequestor().getName(), user2.getName());
    }

}
