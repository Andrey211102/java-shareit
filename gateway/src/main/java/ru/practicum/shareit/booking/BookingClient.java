package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;
import ru.practicum.shareit.booking.exceptions.BookingStatusValidateExeption;
import ru.practicum.shareit.booking.exceptions.BookingValidationException;

import java.time.LocalDateTime;
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

    public ResponseEntity<Object> create(Long bookerId, BookingDto bookingDto) {
        String actionName = "Создание бронирования";
        //Валидация дат
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) throw new BookingValidationException(actionName +
                "Дата начала бронировния находится в прошлом");

        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) throw new BookingValidationException(actionName +
                "Дата начала бронировния позже даты начала");

        return post("", bookerId, bookingDto);
    }

    public ResponseEntity<Object> confirmation(Long ownerItemId, Long bookingId, Boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, (long) ownerItemId);
    }

    public ResponseEntity<Object> getByIdAndBookerOrOwner(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllByState(Long bookerId, String stateBooking, Integer from, Integer size) {

        try {
            BookingState state = BookingState.valueOf(stateBooking);
        } catch (IllegalArgumentException e) {
            throw new BookingStatusValidateExeption("Получение всех бронирований, " +
                    "передано неверное сотстояние " + stateBooking);
        }

        if (from < 0) throw new BookingValidationException("Получен не корректный параметр from: " + from);
        Map<String, Object> parameters = Map.of(
                "state", stateBooking,
                "from", from,
                "size", size);

        return get("?state={state}&from={from}&size={size}", bookerId, parameters);
    }

    public ResponseEntity<Object> getAllByOwnerAndState(Long bookerId, String stateBooking, Integer from, Integer size) {

        try {
            BookingState state = BookingState.valueOf(stateBooking);
        } catch (IllegalArgumentException e) {
            throw new BookingStatusValidateExeption("Получение всех бронирований, " +
                    "передано неверное сотстояние " + stateBooking);
        }

        if (from < 0) throw new BookingValidationException("Получен не корректный параметр from: " + from);

        Map<String, Object> parameters = Map.of(
                "state", stateBooking,
                "from", from,
                "size", size);

        return get("/owner?state={state}&from={from}&size={size}", bookerId, parameters);
    }
}
