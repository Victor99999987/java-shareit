package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    List<Item> getAll();

    Item getById(Long id);

    Item add(Item item);

    Item remove(Long id);

    Item update(Item item);
}
