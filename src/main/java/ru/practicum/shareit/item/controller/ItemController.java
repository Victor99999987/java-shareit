package ru.practicum.shareit.item.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllByUserId(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable Long id) {
        return itemService.getById(id);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @NotNull @Valid @RequestBody ItemDto itemDto) {
        return itemService.add(userId, itemDto);
    }

    @DeleteMapping("/{id}")
    public ItemDto remove(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long id) {
        return itemService.remove(userId, id);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long id,
                          @NotNull @RequestBody ItemDto itemDto) {
        return itemService.update(userId, id, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(defaultValue = "") String text) {
        return itemService.search(text);
    }

}
