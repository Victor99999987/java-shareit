package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    List<User> getAll();

    User getById(Long id);

    User add(User user);

    User remove(Long id);

    User update(User user);
}
