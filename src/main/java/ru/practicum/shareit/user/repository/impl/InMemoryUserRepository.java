package ru.practicum.shareit.user.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.exception.UserAlreadyExistException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, Long> userEmails = new HashMap<>();
    private Long generateId = 0L;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Long id) {
        if (!users.containsKey(id)) {
            log.info(String.format("UserNotFoundException: Не найден пользователь с id=%d", id));
            throw new UserNotFoundException(String.format("Не найден пользователь с id=%d", id));
        }
        return users.get(id);
    }

    @Override
    public User add(User user) {
        if (userEmails.containsKey(user.getEmail())) {
            log.info(String.format("UserAlreadyExistException: Пользователь с email=%s уже существует", user.getEmail()));
            throw new UserAlreadyExistException(String.format("Пользователь с email=%s уже существует", user.getEmail()));
        }
        user.setId(++generateId);
        users.put(user.getId(), user);
        userEmails.put(user.getEmail(), user.getId());
        return user;
    }

    @Override
    public User remove(Long id) {
        User user = getById(id);
        users.remove(id);
        userEmails.remove(user.getEmail());
        return user;
    }

    @Override
    public User update(User user) {
        User oldUser = getById(user.getId());
        if (!Objects.equals(oldUser.getEmail(), user.getEmail()) && userEmails.containsKey(user.getEmail())) {
            log.info(String.format("UserAlreadyExistException: Пользователь с email=%s уже существует", user.getEmail()));
            throw new UserAlreadyExistException(String.format("Пользователь с email=%s уже существует", user.getEmail()));
        }
        users.put(user.getId(), user);
        userEmails.remove(oldUser.getEmail());
        userEmails.put(user.getEmail(), user.getId());
        return user;
    }
}
