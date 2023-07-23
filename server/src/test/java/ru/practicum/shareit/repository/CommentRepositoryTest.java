package ru.practicum.shareit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class CommentRepositoryTest {

    private final Sort byId = Sort.by(Sort.Direction.ASC, "id");
    private final Pageable pageable = PageRequest.of(0, 10, byId);
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CommentRepository commentRepository;
    private User user;
    private User user2;
    private User user3;
    private Item item;
    private Item item2;
    private Comment comment;
    private Comment comment2;

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
        item = Item.builder()
                .name("скотч")
                .description("хороший")
                .owner(user)
                .available(true)
                .build();
        item2 = Item.builder()
                .name("вешалка")
                .description("красивая")
                .owner(user2)
                .available(true)
                .build();
        comment = Comment.builder()
                .text("первый коммент")
                .author(user2)
                .created(LocalDateTime.now())
                .item(item)
                .build();
        comment2 = Comment.builder()
                .text("второй коммент")
                .author(user3)
                .created(LocalDateTime.now())
                .item(item)
                .build();

        user = userRepository.save(user);
        user2 = userRepository.save(user2);
        user3 = userRepository.save(user3);

        item = itemRepository.save(item);
        item2 = itemRepository.save(item2);

        comment = commentRepository.save(comment);
        comment2 = commentRepository.save(comment2);
    }

    @Test
    public void findAllByItem_whenCommentNotFound_ThenReturnEmptyList() {
        List<Comment> result = commentRepository.findAllByItem(item2, byId);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllByItem_whenCommentFound_ThenReturnList() {
        List<Comment> result = commentRepository.findAllByItem(item, byId);

        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getText(), comment.getText());
        assertEquals(result.get(1).getText(), comment2.getText());
    }

    @Test
    public void findAllByItemIn_whenCommentNotFound_ThenReturnEmptyList() {
        List<Comment> result = commentRepository.findAllByItemIn(List.of(item2), byId);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllByItemIn_whenCommentFound_ThenReturnList() {
        List<Comment> result = commentRepository.findAllByItemIn(List.of(item, item2), byId);

        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getText(), comment.getText());
        assertEquals(result.get(1).getText(), comment2.getText());
    }

}
