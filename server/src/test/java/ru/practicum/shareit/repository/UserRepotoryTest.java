package ru.practicum.shareit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepotoryTest {

    @Autowired
    UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();
    }

    @Test
    public void existsUserByEmail_whenUserNotFound_ThenReturnFalse() {
        boolean result = userRepository.existsUserByEmail("user@user.com");

        assertFalse(result);
    }

    @Test
    public void existsUserByEmail_whenUserFound_ThenReturnTrue() {
        userRepository.save(user);

        boolean result = userRepository.existsUserByEmail("user@user.com");

        assertTrue(result);
    }

}
