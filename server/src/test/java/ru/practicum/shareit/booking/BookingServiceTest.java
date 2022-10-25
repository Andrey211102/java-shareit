package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfDto;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.BookingStatusValidateExeption;
import ru.practicum.shareit.booking.exceptions.BookingValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestsStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class BookingServiceTest {

    @Autowired
    BookingService service;
    @Autowired
    BookingDtoMapper mapper;
    @Autowired
    BookingStorage storage;
    @Autowired
    ItemStorage itemStorage;
    @Autowired
    ItemRequestsStorage requestsStorage;
    @Autowired
    UserStorage userStorage;
    @Autowired
    BookingStorage bookingStorage;

    private User user;
    private User user2;
    private Item item;
    private Item item2;
    private ItemRequest request;
    private BookingDto dto;

    private Booking booking;

    @BeforeEach
    void setUp() {

        user = userStorage.save(new User(null, "testUser", "user@mail.test"));
        user2 = userStorage.save(new User(null, "testUser2", "user2@mail.test"));
        request = requestsStorage.save(new ItemRequest(1L, "Тестовое описание", LocalDateTime.now(), user));

        item = itemStorage.save(new Item(1L, "Дрель", "Описание тест", true, user, request));
        item2 = itemStorage.save(new Item(2L, "Кусачки", "Описание тест", false, user, null));

        dto = new BookingDto(1L, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(10));

        booking = bookingStorage.save(mapper.fromDto(dto, item2, user, BookingStatus.WAITING));
    }

    //create
    @Test
    void shouldThrowBookingValidationExceptionCreateIncorrectDateStart() {
        dto.setStart(LocalDateTime.now().minusHours(1));
        assertThrows(BookingValidationException.class, () -> service.create(item.getOwner().getId(), dto));
    }

    @Test
    void shouldThrowBookingValidationExceptionCreateIncorrectDateEnd() {
        dto.setEnd(dto.getStart().minusHours(1));
        assertThrows(BookingValidationException.class, () -> service.create(item.getOwner().getId(), dto));
    }

    @Test
    void shouldThrowItemNotFoundExceptionCreateIncorrectItem() {
        dto.setItemId(100L);
        assertThrows(ItemNotFoundException.class, () -> service.create(item.getOwner().getId(), dto));
    }

    @Test
    void shouldThrowUserNotFoundExceptionCreateIncorrectUser() {
        dto.setItemId(item.getId());
        assertThrows(UserNotFoundException.class, () -> service.create(100L, dto));
    }

    @Test
    void shouldThrowItemNotFoundExceptionCreateOwner() {
        assertThrows(ItemNotFoundException.class, () -> service.create(user.getId(), dto));
    }

    @Test
    void shouldDoesNotThrowCreate() {
        dto.setItemId(item.getId());
        assertDoesNotThrow(() -> service.create(user2.getId(), dto));
    }

    //confirmation
    @Test
    void shouldDoesNotThrowConfirmation() {
        assertDoesNotThrow(() -> service.confirmation(booking.getItem().getOwner().getId(), booking.getId(), true));
    }

    @Test
    void shouldThrowBookingNotFoundExceptionConfirmationUnknowBooking() {
        assertThrows(BookingNotFoundException.class, () -> service.confirmation(booking.getItem().getOwner().getId(),
                100L, true));
    }

    @Test
    void shouldThrowBookingValidationExceptionConfirmationApproved() {
        service.confirmation(booking.getItem().getOwner().getId(), booking.getId(), true);
        assertThrows(BookingValidationException.class, () -> service.confirmation(booking.getItem().getOwner().getId(),
                booking.getId(), true));
    }

    //getByIdAndBookerOrOwner
    @Test
    void shouldEqualsIdGetByIdAndBookerOrOwner() {
        BookingInfDto finded = service.getByIdAndBookerOrOwner(booking.getBooker().getId(), booking.getId());
        assertEquals(booking.getId(), finded.getId());
    }

    @Test
    void shouldThrowBookingNotFoundExceptionGetByIdAndBookerOrOwnerIncorrectUser() {
        assertThrows(BookingNotFoundException.class,
                () -> service.getByIdAndBookerOrOwner(100L, booking.getId()));
    }

    //getAllByState
    @Test
    void shouldEqualsCountGetAllByState() {
        List<BookingInfDto> finded = service.getAllByState(booking.getBooker().getId(),
                BookingStatus.WAITING.name(), 0, 10);
        assertEquals(1, finded.size());
    }

    @Test
    void shouldThrowBookingStatusValidateExeptionGetAllByStateInvalidStatus() {

        assertThrows(BookingStatusValidateExeption.class,
                () -> service.getAllByState(booking.getBooker().getId(),
                        "1111", 0, 10));
    }

    @Test
    void shouldEqualsIdsByStateCURRENTGetAllByState() {

        dto.setStart(LocalDateTime.now().minusHours(1));
        Booking booking2 = bookingStorage.save(mapper.fromDto(dto, item2, user, BookingStatus.WAITING));

        List<BookingInfDto> finded = service.getAllByState(booking.getBooker().getId(),
                BookingState.CURRENT.name(), 0, 10);

        assertEquals(booking2.getId(), finded.get(0).getId());
    }

    @Test
    void shouldEqualsIdsByStatePASTGetAllByState() {

        dto.setStart(LocalDateTime.now().minusHours(2));
        dto.setEnd(LocalDateTime.now().minusHours(1));

        Booking booking2 = bookingStorage.save(mapper.fromDto(dto, item2, user, BookingStatus.WAITING));

        List<BookingInfDto> finded = service.getAllByState(booking.getBooker().getId(),
                BookingState.PAST.name(), 0, 10);

        assertEquals(booking2.getId(), finded.get(0).getId());
    }

    @Test
    void shouldEqualsIdsByStateFUTUREGetAllByState() {
        List<BookingInfDto> finded = service.getAllByState(booking.getBooker().getId(),
                BookingState.FUTURE.name(), 0, 10);

        assertEquals(booking.getId(), finded.get(0).getId());
    }

    @Test
    void shouldEqualsCountGetAllByOwnerAndState() {

        List<BookingInfDto> finded = service.getAllByOwnerAndState(user2.getId(),
                BookingState.ALL.name(), 0, 10);

        assertEquals(0, finded.size());
    }

    @Test
    void shouldThrowBookingStatusValidateExeptionGetAllByOwnerAndStateInvalidState() {

        assertThrows(BookingStatusValidateExeption.class,
                () -> service.getAllByOwnerAndState(booking.getBooker().getId(),
                        "1111", 0, 10));
    }

    @Test
    void shouldThrowUserNotFoundExceptionGetAllByOwnerAndStateUnknowOwner() {
        assertThrows(UserNotFoundException.class,
                () -> service.getAllByOwnerAndState(100L,
                        BookingState.ALL.name(), 0, 10));
    }

    @Test
    void shouldEqualsCountByStateFUTUREgetAllByOwnerAndState() {
        List<BookingInfDto> finded = service.getAllByOwnerAndState(booking.getBooker().getId(),
                BookingState.FUTURE.name(), 0, 10);

        assertEquals(booking.getId(), finded.get(0).getId());
    }

    @Test
    void shouldEqualsIdsByStateCURRENTgetAllByOwnerAndState() {

        dto.setStart(LocalDateTime.now().minusHours(1));
        Booking booking2 = bookingStorage.save(mapper.fromDto(dto, item2, user, BookingStatus.WAITING));

        List<BookingInfDto> finded = service.getAllByOwnerAndState(booking.getBooker().getId(),
                BookingState.CURRENT.name(), 0, 10);

        assertEquals(booking2.getId(), finded.get(0).getId());
    }

    @Test
    void shouldEqualsCountsByStateWAITINGgetAllByOwnerAndState() {

        dto.setStart(LocalDateTime.now().minusHours(1));
        Booking booking2 = bookingStorage.save(mapper.fromDto(dto, item2, user, BookingStatus.WAITING));

        List<BookingInfDto> finded = service.getAllByOwnerAndState(booking.getBooker().getId(),
                BookingState.WAITING.name(), 0, 10);

        assertEquals(2, finded.size());
    }

    @Test
    void shouldEqualsCountsByStatePASTgetAllByOwnerAndState() {

        dto.setStart(LocalDateTime.now().minusHours(2));
        dto.setEnd(LocalDateTime.now().minusHours(1));
        Booking booking2 = bookingStorage.save(mapper.fromDto(dto, item2, user, BookingStatus.WAITING));

        List<BookingInfDto> finded = service.getAllByOwnerAndState(booking2.getBooker().getId(),
                BookingState.PAST.name(), 0, 10);

        assertEquals(1, finded.size());
    }
}