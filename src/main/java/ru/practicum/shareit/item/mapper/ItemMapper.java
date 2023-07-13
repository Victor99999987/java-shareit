package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;

@UtilityClass
public class ItemMapper {
    public static ItemDto toDto(Item item) {
        Long requestId = item.getRequest() != null ? item.getRequest().getId() : null;

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(requestId)
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        Long requestId = itemDto.getRequestId() != null ? itemDto.getRequestId() : null;
        Request request = Request.builder()
                .id(requestId)
                .build();

        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(request)
                .build();
    }
}
