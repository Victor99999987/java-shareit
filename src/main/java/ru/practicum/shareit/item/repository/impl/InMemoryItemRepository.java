package ru.practicum.shareit.item.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.*;

@Slf4j
@Component
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long generateId = 0L;

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item getById(Long id) {
        if (!items.containsKey(id)) {
            log.info(String.format("ItemNotFoundException: Не найден пользователь с id=%d", id));
            throw new ItemNotFoundException(String.format("Не найден пользователь с id=%d", id));
        }
        return items.get(id);
    }

    @Override
    public Item add(Item item) {
        item.setId(++generateId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item remove(Long id) {
        Item item = getById(id);
        items.remove(id);
        return item;
    }

    @Override
    public Item update(Item item) {
        Long id = item.getId();
        Item oldItem = getById(id);
        Long ownerId = oldItem.getOwner().getId();
        if (!Objects.equals(ownerId, item.getOwner().getId())) {
            log.info(String.format("ItemNotFoundException: У вещи с id=%d другой владелец", id));
            throw new ItemNotFoundException(String.format("У вещи с id=%d другой владелец", id));
        }
        items.put(item.getId(), item);
        return item;
    }
}
