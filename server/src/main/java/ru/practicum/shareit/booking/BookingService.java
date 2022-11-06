package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.exceptions.BoockingStateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfDto;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
//import ru.practicum.shareit.booking.exceptions.BookingStatusValidateExeption;
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

        //Предмет есть
        Item item = itemStorage.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException(actionName + ", по переданному id не найден предмет!"));

        //Предмет доступен
        if (!item.isAvailable()) throw new BoockingStateException(actionName + " " + item.getName() +
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

        if (booking.getStatus() == BookingStatus.APPROVED) throw new BoockingStateException(action +
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

    public List<BookingInfDto> getAllByState(Long bookerId, String stateBooking, Integer from, Integer size) {

        BookingState state;

        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);

        state = BookingState.valueOf(stateBooking);

        User booker = userStorage.findById(bookerId).orElseThrow(() -> new UserNotFoundException(
                "Получение бронирований по статусу, не найден заказчик с id: " + bookerId));

        Page<Booking> bookings;

        switch (state) {
            case CURRENT:
                bookings = storage.findByBookerAndDatesCurrent(bookerId, LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                bookings = storage.findByBookerAndDatesPast(bookerId, LocalDateTime.now(), pageRequest);
                break;
            case REJECTED:
                bookings = storage.findAllByBooker_IdAndStatusOrderByStartDesc(booker.getId(), BookingStatus.REJECTED,
                        pageRequest);
                break;
            case WAITING:
                bookings = storage.findAllByBooker_IdAndStatusOrderByStartDesc(booker.getId(), BookingStatus.WAITING,
                        pageRequest);
                break;
            case FUTURE:
                bookings = storage.findByBookerAndDatesFuture(bookerId, LocalDateTime.now(), pageRequest);
                break;
            default:
                bookings = storage.findAllByBooker_IdOrderByStartDesc(booker.getId(), pageRequest);
        }

        return bookings.stream()
                .map(booking -> mapper.toInfDto(booking))
                .collect(Collectors.toList());
    }

    public List<BookingInfDto> getAllByOwnerAndState(Long ownerId, String stateBooking, Integer from, Integer size) {

        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        BookingState state = BookingState.valueOf(stateBooking);

        User owner = userStorage.findById(ownerId).orElseThrow(() -> new UserNotFoundException(
                "Получение бронирований по статусу, не найден владелец предмета с id: " + ownerId));

        Page<Booking> bookings;

        switch (state) {
            case CURRENT:
                bookings = storage.findByOwnerAndDatesCurrent(ownerId, LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                bookings = storage.findByOwnerAndDatesPast(ownerId, LocalDateTime.now(), pageRequest);
                break;
            case REJECTED:
                bookings = storage.findAllByItem_Owner_IdAndStatusOrderByStartDesc(owner.getId(),
                        BookingStatus.REJECTED, pageRequest);
                break;
            case WAITING:
                bookings = storage.findAllByItem_Owner_IdAndStatusOrderByStartDesc(owner.getId(),
                        BookingStatus.WAITING, pageRequest);
                break;
            case FUTURE:
                bookings = storage.findByOwnerAndDatesFuture(ownerId, LocalDateTime.now(), pageRequest);
                break;
            default:
                bookings = storage.findAllByItem_Owner_IdOrderByStartDesc(owner.getId(), pageRequest);
        }
        return bookings.stream()
                .map(booking -> mapper.toInfDto(booking))
                .collect(Collectors.toList());
    }
}
