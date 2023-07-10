package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;

@UtilityClass
public class RequestMapper {
    public static RequestDto toDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }

    public static Request toRequest(RequestDto requestDto) {
        return Request.builder()
                .id(requestDto.getId())
                .description(requestDto.getDescription())
                .created(requestDto.getCreated())
                .build();
    }
}
