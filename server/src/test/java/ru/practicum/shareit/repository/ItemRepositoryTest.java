package ru.practicum.shareit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRepositoryTest {

    private final Sort byId = Sort.by(Sort.Direction.ASC, "id");
    private final Pageable pageable = PageRequest.of(0, 10, byId);
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    RequestRepository requestRepository;
    private User user;
    private User user2;
    private User user3;
    private Item item;
    private Item item2;
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
        item = Item.builder()
                .name("скотч")
                .description("хороший")
                .owner(user)
                .available(true)
                .request(request)
                .build();
        item2 = Item.builder()
                .name("вешалка")
                .description("красивая")
                .owner(user2)
                .available(true)
                .build();

        user = userRepository.save(user);
        user2 = userRepository.save(user2);
        user3 = userRepository.save(user3);

        request = requestRepository.save(request);
        request2 = requestRepository.save(request2);

        item = itemRepository.save(item);
        item2 = itemRepository.save(item2);
    }

    @Test
    public void findAllByOwner_whenItemNotFound_ThenReturnEmptyList() {
        List<Item> result = itemRepository.findAllByOwner(user3, pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllByOwner_whenItemFound_ThenReturnList() {
        List<Item> result = itemRepository.findAllByOwner(user, pageable);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getOwner().getName(), user.getName());
        assertEquals(result.get(0).getName(), item.getName());
    }

    @Test
    public void findAllByNameContaining_whenItemNotFound_ThenReturnEmptyList() {
        List<Item> result = itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(
                "самолет", "самолет", true, pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllByNameContaining_whenItemFound_ThenReturnList() {
        List<Item> result = itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(
                "веша", "веша", true, pageable);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getOwner().getName(), user2.getName());
        assertEquals(result.get(0).getName(), item2.getName());
    }

    @Test
    public void findAllByRequestIn_whenItemNotFound_ThenReturnEmptyList() {
        List<Item> result = itemRepository.findAllByRequestIn(List.of(request2), byId);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllByRequestIn_whenItemFound_ThenReturnList() {
        List<Item> result = itemRepository.findAllByRequestIn(List.of(request), byId);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getRequest().getId(), request.getId());
    }

    @Test
    public void findAllByRequest_whenItemNotFound_ThenReturnEmptyList() {
        List<Item> result = itemRepository.findAllByRequest(request2, byId);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllByRequest_whenItemFound_ThenReturnList() {
        List<Item> result = itemRepository.findAllByRequest(request, byId);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getRequest().getId(), request.getId());
    }

}
