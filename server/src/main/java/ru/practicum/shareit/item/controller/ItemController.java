package ru.practicum.shareit.item.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
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
    public List<ItemDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        return itemService.findAllByUserId(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @PathVariable Long id) {
        return itemService.findById(userId, id);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @NotNull @Valid @RequestBody ItemDto itemDto) {
        return itemService.add(userId, itemDto);
    }

    @DeleteMapping("/{id}")
    public void remove(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @PathVariable Long id) {
        itemService.remove(userId, id);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long id,
                          @NotNull @RequestBody ItemDto itemDto) {
        return itemService.update(userId, id, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(defaultValue = "") String text,
                                @RequestParam(defaultValue = "0") int from,
                                @RequestParam(defaultValue = "10") int size) {
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @NotNull @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
