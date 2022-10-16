package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    BookingService service;

    static BookingDto dto;
    static BookingInfDto infDto;
    static BookingInfDto infDto2;
    static BookingInfDto infDto3;
    static List<BookingInfDto> dtos;

    @BeforeAll
    static void beforeAll() {

        dto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1L));

        infDto = new BookingInfDto(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1L), BookingStatus.WAITING,
                new BookingInfDto.BookerDto(1L, "Иван"), new BookingInfDto.BookingItemDto(1L, "Дрель"));

        infDto2 = new BookingInfDto(2L, LocalDateTime.now(), LocalDateTime.now().plusHours(1L),
                BookingStatus.APPROVED, new BookingInfDto.BookerDto(1L, "Иван"),
                new BookingInfDto.BookingItemDto(1L, "Дрель"));

        infDto3 = new BookingInfDto(3L, LocalDateTime.now(), LocalDateTime.now().plusHours(1L),
                BookingStatus.APPROVED, new BookingInfDto.BookerDto(1L, "Иван"),
                new BookingInfDto.BookingItemDto(1L, "Кусачки"));

        dtos = Arrays.asList(infDto, infDto2, infDto3);
    }

    @Test
    void shouldEqualsPropertysCreateAndVerify() throws Exception {

        when(service.create(any(Long.class), any(BookingDto.class)))
                .thenReturn(infDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(infDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(infDto.getId()), Long.class))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.status", is(infDto.getStatus().name())))
                .andExpect(jsonPath("$.booker.id", is(infDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(infDto.getItem().getId()), Long.class));

        verify(service, times(1)).create(any(Long.class), any(BookingDto.class));
    }

    @Test
    void shouldEqualsStatusConfirmation() throws Exception {

        when(service.confirmation(1L, 1L, true))
                .thenReturn(infDto3);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void shouldReturnOkGetById() throws Exception {

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnErrorGetByIdIncorrectId() throws Exception {

        mockMvc.perform(get("/bookings/x")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldReturnOAndEqualsSizeGetAllByOwnerAndState() throws Exception {

        when(service.getAllByOwnerAndState(1L, BookingStatus.APPROVED.name(), PageRequest.of(0, 10)))
                .thenReturn(Arrays.asList(infDto2, infDto3));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", BookingStatus.APPROVED.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldReturn4xxGetAllByOwnerAndStateWhenIncorrectFrom() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldEqualsSizeReturnGetAllByStateAndReturnStatusOk() throws Exception {

        when(service.getAllByState(1L, BookingStatus.APPROVED.name(), PageRequest.of(0, 10)))
                .thenReturn(Arrays.asList(infDto2, infDto3));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", BookingStatus.APPROVED.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(service, times(1)).getAllByState(1L,
                BookingStatus.APPROVED.name(), PageRequest.of(0, 10));
    }
}