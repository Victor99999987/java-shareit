package ru.practicum.shareit.item.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "10") int size) {
        return itemClient.findAllByUserId(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long id) {
        return itemClient.findById(userId, id);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @NotNull @Valid @RequestBody ItemDto itemDto) {
        return itemClient.add(userId, itemDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> remove(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long id) {
        return itemClient.remove(userId, id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long id,
                                         @NotNull @RequestBody ItemDto itemDto) {
        return itemClient.update(userId, id, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(defaultValue = "") String text,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @NotNull @Valid @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }

}
