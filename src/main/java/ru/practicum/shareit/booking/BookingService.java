package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfDto;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.BookingStatusValidateExeption;
import ru.practicum.shareit.booking.exceptions.BookingValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingStorage storage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    private final BookingDtoMapper mapper;

    public BookingInfDto create(Long bookerId, BookingDto bookingDto) {

        String actionName = "Создание бронирования";

        //Валидация дат
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) throw new BookingValidationException(actionName +
                "Дата начала бронировния находится в прошлом");

        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) throw new BookingValidationException(actionName +
                "Дата начала бронировния позже даты начала");

        //Предмет есть
        Item item = itemStorage.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException(actionName + ", по переданному id не найден предмет!"));

        //Предмет доступен
        if (!item.isAvailable()) throw new BookingValidationException(actionName + " " + item.getName() +
                ", не доступен!");

        //Владелец
        User booker = userStorage.findById(bookerId)
                .orElseThrow(() -> new UserNotFoundException(actionName + ", по переданному id не найден владелец!"));

        //Своя же вещь
        if (item.getOwner().getId() == bookerId.longValue()) throw new ItemNotFoundException(actionName +
                ", нельзя забронировать свою вещь!");

        Booking booking = mapper.fromDto(bookingDto, item, booker, BookingStatus.WAITING);
        log.info("Добавлено новое бронирование предмета: {}", booking.getItem().getName());

        return mapper.toInfDto(storage.save(booking));
    }

    public BookingInfDto confirmation(Long ownerId, Long bookingId, Boolean approved) {

        String action = "Подтверждение бронирования";

        Booking booking = storage.findByIdAndItem_Owner_Id(bookingId, ownerId);

        if (booking == null) throw new BookingNotFoundException(action + ", не найдено бронирование " +
                "по id: " + bookingId + " и id владельца предмета: " + ownerId);

        if (booking.getStatus() == BookingStatus.APPROVED) throw new BookingValidationException(action +
                ", бронирование уже подтверждено");

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        log.info("Бронирование id:" + booking.getId() + ", Установлен статус " + booking.getStatus().toString());

        return mapper.toInfDto(storage.save(booking));
    }

    public BookingInfDto getByIdAndBookerOrOwner(Long userId, Long bookingId) {

        Booking booking = storage.findByIdAndBookerOrOwner(bookingId, userId);
        if (booking == null) throw new BookingNotFoundException("Получение бронирования, не найдено бронирование по " +
                "id: " + bookingId + " и id пользователя: " + userId);

        return mapper.toInfDto(booking);
    }

    public List<BookingInfDto> getAllByState(Long bookerId, String stateBooking) {

        BookingState state;
        try {
            state = BookingState.valueOf(stateBooking);
        } catch (IllegalArgumentException e) {
            throw new BookingStatusValidateExeption("Получение всех бронирований, " +
                    "передано неверное сотстояние " + stateBooking);
        }

        User booker = userStorage.findById(bookerId).orElseThrow(() -> new UserNotFoundException(
                "Получение бронирований по статусу, не найден заказчик с id: " + bookerId));

        List<Booking> bookings;

        switch (state) {
            case CURRENT:
                bookings = storage.findByBookerAndDatesCurrent(bookerId, LocalDateTime.now());
                break;
            case PAST:
                bookings = storage.findByBookerAndDatesPast(bookerId, LocalDateTime.now());
                break;
            case REJECTED:
                bookings = storage.findAllByBooker_IdAndStatusOrderByStartDesc(booker.getId(), BookingStatus.REJECTED);
                break;
            case WAITING:
                bookings = storage.findAllByBooker_IdAndStatusOrderByStartDesc(booker.getId(), BookingStatus.WAITING);
                break;
            case FUTURE:
                bookings = storage.findByBookerAndDatesFuture(bookerId, LocalDateTime.now());
                break;
            default:
                bookings = storage.findAllByBooker_IdOrderByStartDesc(booker.getId());
        }

        return bookings.stream()
                .map(booking -> mapper.toInfDto(booking))
                .collect(Collectors.toList());
    }

    public List<BookingInfDto> getAllByOwnerAndState(Long ownerId, String stateBooking) {

        BookingState state;
        try {
            state = BookingState.valueOf(stateBooking);
        } catch (IllegalArgumentException e) {
            throw new BookingStatusValidateExeption("Получение всех бронирований, " +
                    "передано неверное сотстояние " + stateBooking);
        }

        User owner = userStorage.findById(ownerId).orElseThrow(() -> new UserNotFoundException(
                "Получение бронирований по статусу, не найден владелец предмета с id: " + ownerId));

        List<Booking> bookings;

        switch (state) {
            case CURRENT:
                bookings = storage.findByOwnerAndDatesCurrent(ownerId, LocalDateTime.now());
                break;
            case PAST:
                bookings = storage.findByOwnerAndDatesPast(ownerId, LocalDateTime.now());
                break;
            case REJECTED:
                bookings = storage.findAllByItem_Owner_IdAndStatusOrderByStartDesc(owner.getId(),
                        BookingStatus.REJECTED);
                break;
            case WAITING:
                bookings = storage.findAllByItem_Owner_IdAndStatusOrderByStartDesc(owner.getId(),
                        BookingStatus.WAITING);
                break;
            case FUTURE:
                bookings = storage.findByOwnerAndDatesFuture(ownerId, LocalDateTime.now());
                break;
            default:
                bookings = storage.findAllByItem_Owner_IdOrderByStartDesc(owner.getId());
        }
        return bookings.stream()
                .map(booking -> mapper.toInfDto(booking))
                .collect(Collectors.toList());
    }
}
