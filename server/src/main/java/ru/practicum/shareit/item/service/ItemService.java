package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> findAllByUserId(Long userId, int from, int size);

    ItemDto findById(Long userId, Long id);

    ItemDto add(Long userId, ItemDto itemDto);

    void remove(Long userId, Long id);

    ItemDto update(Long userId, Long id, ItemDto itemDto);

    List<ItemDto> search(String text, int from, int size);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
