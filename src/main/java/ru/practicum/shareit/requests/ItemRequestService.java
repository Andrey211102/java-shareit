package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestInfDto;
import ru.practicum.shareit.requests.exceptions.ItemRequestNotFoundExeption;
import ru.practicum.shareit.requests.exceptions.ItemRequestValidationExeption;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestsStorage storage;
    private final ItemRequestDtoMapper mapper;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    public ItemRequestInfDto create(long userId, ItemRequestDto dto) {

        String action = "Добавление запроса";
        //Проверки
        if (userId <= 0) throw new ItemRequestValidationExeption(action +
                ", передан не корректный id пользователя: " + userId);

        User user = userStorage.findById(userId).orElseThrow(() -> new UserNotFoundException(action + "," +
                "не найден пользователь по id:  " + userId));

        ItemRequest request = storage.save(mapper.fromDto(dto, user));
        log.info("Добавлен новый запрос id: {}", request.getId());

        return mapper.toInfDto(request, new ArrayList<>());
    }

    public ItemRequestInfDto getById(long userId, long id) {

        String action = "Получение запроса по id";
        User user = userStorage.findById(userId).orElseThrow(() -> new UserNotFoundException(action + "," +
                "не найден пользователь по id:  " + userId));

        ItemRequest request = storage.findById(id)
                .orElseThrow(() -> new ItemRequestNotFoundExeption(action + " , не найден запрос по id: " + id));

        return mapper.toInfDto(request, itemStorage.findAllByRequest_Id(id));
    }

    public List<ItemRequestInfDto> getByUserid(long userId, Integer from, Integer size) {

        if (from < 0) throw new ItemRequestValidationExeption("Получен не корректный параметр from: " + from);
        int page = from / size;

        PageRequest pageRequest = PageRequest.of(page, size);

        String action = "Получение запросов по id пользователя";
        User user = userStorage.findById(userId).orElseThrow(() -> new UserNotFoundException(action + "," +
                "не найден пользователь по id:  " + userId));

        return storage.findByUser(userId, pageRequest)
                .stream()
                .map(request -> mapper.toInfDto(request, itemStorage.findAllByRequest_Id(request.getId())))
                .collect(Collectors.toList());
    }

    public List<ItemRequestInfDto> getAll(Long userId, Integer from, Integer size) {

        if (from < 0) throw new ItemRequestValidationExeption("Получен не корректный параметр from: " + from);

        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);

        String action = "Получение запросов других пользователей";
        User user = userStorage.findById(userId).orElseThrow(() -> new UserNotFoundException(action + "," +
                "не найден пользователь по id:  " + userId));

        return storage.findAllOtherUsers(user.getId(), pageRequest).stream()
                .map(request -> mapper.toInfDto(request, itemStorage.findAllByRequest_Id(request.getId())))
                .collect(Collectors.toList());
    }
}
