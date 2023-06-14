package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public List<ItemDto> getAllByUserId(Long userId) {
        return itemRepository.getAll()
                .stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), userId))
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    public ItemDto getById(Long id) {
        return ItemMapper.toDto(itemRepository.getById(id));
    }

    public ItemDto add(Long userId, ItemDto itemDto) {
        User user = userRepository.getById(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return ItemMapper.toDto(itemRepository.add(item));
    }

    public ItemDto remove(Long userId, Long id) {
        User user = userRepository.getById(userId);
        Item item = itemRepository.getById(id);
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            log.info(String.format("ItemNotFoundException: У вещи с id=%d другой владелец", id));
            throw new ItemNotFoundException(String.format("У вещи с id=%d другой владелец", id));
        }
        return ItemMapper.toDto(itemRepository.remove(id));
    }

    public ItemDto update(Long userId, Long id, ItemDto itemDto) {
        User user = userRepository.getById(userId);
        Item oldItem = itemRepository.getById(id);
        Item item = ItemMapper.toItem(itemDto);
        item.setId(id);
        item.setOwner(user);
        if (item.getName() == null) {
            item.setName(oldItem.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(oldItem.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(oldItem.getAvailable());
        }
        return ItemMapper.toDto(itemRepository.update(item));
    }

    public List<ItemDto> search(String text) {
        if (text.equals("")) {
            return List.of();
        }
        String lowerText = text.toLowerCase();
        return itemRepository.getAll()
                .stream()
                .filter(item -> item.getAvailable() && (item.getName().toLowerCase().contains(lowerText)
                        || item.getDescription().toLowerCase().contains(lowerText)))
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }
}
