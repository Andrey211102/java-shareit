package ru.practicum.shareit.item.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.QBooking;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfDto;
import ru.practicum.shareit.item.exceptions.ItemAddCommentException;
import ru.practicum.shareit.item.exceptions.ItemForbiddenException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestsStorage;
import ru.practicum.shareit.requests.exceptions.ItemRequestNotFoundExeption;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage storage;
    private final ItemRequestsStorage requestsStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentStrorage commentStrorage;
    private final CommentDtoMapper commentMapper;
    private final ItemDtoMapper mapper;

    @Override
    public ItemDto create(long idOwner, ItemDto dto) {

        String action = "Добавление Item";

        //Проверки
        User owner = userStorage.findById(idOwner).orElseThrow(() -> new UserNotFoundException(action + "," +
                "не найден владелец по id:  " + idOwner));

        Optional<ItemRequest> request = Optional.empty();

        if (dto.getRequestId() != null) {
            request = requestsStorage.findById(dto.getRequestId());
            if (request.isEmpty()) throw
                    new ItemRequestNotFoundExeption(action + ",не найден запрос по id:  " + dto.getRequestId());
        }

        //Сохранение
        Item item = mapper.fromDto(dto, owner, request);
        Item createdItem = storage.save(item);

        log.info("Добавлен новый предмет: {}", createdItem.getName());

        return mapper.toDto(createdItem);
    }

    @Override
    public ItemInfDto getById(long id, long userId) {

        Item item = storage.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Получение по Id, не найден по id: " + id));

        return mapper.toInfDto(item,
                bookingStorage.getFirstByItem_IdAndItem_Owner_IdOrderByEnd(id, userId),
                bookingStorage.getFirstByItem_IdAndAndItem_Owner_IdOrderByStartDesc(id, userId),
                commentStrorage.findAllByItem_Id(id));
    }

    @Override
    public ItemDto update(long idOwner, long id, ItemDto itemDto) {

        //Проверки
        String action = "Обновление Item";

        Item item = storage.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(action + ", не найден по id: " + id));

        if (item.getOwner().getId() != idOwner) throw new ItemForbiddenException(action + ", передан" +
                " не верный id владельца");

        //Сохранение
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());

        storage.save(item);
        log.info("Обновлен предмет : {}", item.getName());

        return mapper.toDto(item);
    }

    @Override
    public List<ItemInfDto> getByOwner(long idOwner, Integer from, Integer size) {

        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);

        return storage.findByOwner_IdOrderById(idOwner, pageRequest)
                .stream()
                .map(item -> mapper.toInfDto(item,
                        bookingStorage.getFirstByItem_IdAndItem_Owner_IdOrderByEnd(item.getId(), idOwner),
                        bookingStorage.getFirstByItem_IdAndAndItem_Owner_IdOrderByStartDesc(item.getId(), idOwner),
                        commentStrorage.findAllByItem_Id(item.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public CommentInfDto addComment(Long authorId, Long itemId, CommentDto dto) {

        //Проверки
        String action = "Добавление комментария";

        Item item = storage.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(action + ", не найден предмет по id: " + itemId));

        User author = userStorage.findById(authorId).orElseThrow(() -> new UserNotFoundException(action + "," +
                "не найден владелец по id:  " + authorId));

        //Автор брал эту вещь
        BooleanExpression byBookerId = QBooking.booking.booker.id.eq(authorId);
        BooleanExpression byItemId = QBooking.booking.item.id.eq(itemId);
        BooleanExpression byStatus = QBooking.booking.status.eq(BookingStatus.APPROVED);
        BooleanExpression byLate = QBooking.booking.end.before(LocalDateTime.now());

        if (!bookingStorage.findAll(byBookerId.and(byStatus).and(byItemId).and(byLate))
                .iterator()
                .hasNext()) throw new ItemAddCommentException(action + ", автор не бронировал этот предмет!");

        log.info("Добавлен комментарий к предмету: {}", item.getName());

        //Добавление
        return commentMapper.toInfDto(commentStrorage
                .save(commentMapper.fromDto(dto, item, author)));
    }

    @Override
    public List<ItemDto> search(String request, Integer from, Integer size) {

        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);

        if (request.length() == 0) return new ArrayList<>();

        return storage.search(request, pageRequest)
                .stream()
                .map((mapper::toDto))
                .collect(Collectors.toList());
    }
}
