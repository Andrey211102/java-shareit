package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;

import java.util.Map;

@Service
public class RequestsClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestsClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(long userId, ItemRequestDto dto) {

        if (userId <= 0) throw new ItemRequestValidationExeption("Создание запроса" +
                ", передан не корректный id пользователя: " + userId);
        return post("", userId, dto);
    }

    public ResponseEntity<Object> getById(long userId, long requestId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> getByUserid(long userId, Integer from, Integer size) {

        if (from < 0) throw new ItemRequestValidationExeption("Получен не корректный параметр from: " + from);

        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getAll(long userId, Integer from, Integer size) {

        if (from < 0) throw new ItemRequestValidationExeption("Получен не корректный параметр from: " + from);

        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);
        return get("/all?from={from}&size={size}", userId, parameters);
    }
}
