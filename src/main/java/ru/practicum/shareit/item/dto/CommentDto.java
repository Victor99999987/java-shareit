package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    Long id;
    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(min = 1, max = 1000, message = "Текст коммментария должен быть до 1000 символов")
    String text;
    String authorName;
    LocalDateTime created;
}
