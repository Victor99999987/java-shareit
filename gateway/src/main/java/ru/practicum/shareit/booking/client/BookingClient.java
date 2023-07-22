package ru.practicum.shareit.booking.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> add(Long userId, BookingDtoIn bookingDtoIn) {
        if (bookingDtoIn.getStart().isAfter(bookingDtoIn.getEnd())) {
            throw new IllegalArgumentException("Дата начала бронирования позже окончания бронирования");
        }
        if (bookingDtoIn.getStart().equals(bookingDtoIn.getEnd())) {
            throw new IllegalArgumentException("Дата начала и окончания бронирования совпадают");
        }
        return post("", userId, bookingDtoIn);
    }

    public ResponseEntity<Object> update(long userId, Long id, boolean approved) {
        return patch("/" + id + "?approved=" + approved, userId);
    }

    public ResponseEntity<Object> findById(Long userId, Long id) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> findAllByUserId(Long userId, String stateIn, int from, int size) {
        State state;
        try {
            state = State.valueOf(stateIn);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Unknown state: %s", stateIn));
        }
        if (from < 0) {
            throw new IllegalArgumentException("Минимальное значение записи, с которой можно получить данные равно 0");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Количество записей на странице должно быть больше 0");
        }
        Map<String, Object> parameters = Map.of(
                "state", stateIn,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findAllByOwnerId(Long userId, String stateIn, int from, int size) {
        State state;
        try {
            state = State.valueOf(stateIn);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Unknown state: %s", stateIn));
        }

        if (from < 0) {
            throw new IllegalArgumentException("Минимальное значение записи, с которой можно получить данные равно 0");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Количество записей на странице должно быть больше 0");
        }
        Map<String, Object> parameters = Map.of(
                "state", stateIn,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

}
