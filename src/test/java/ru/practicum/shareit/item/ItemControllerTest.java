package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfDto;
import ru.practicum.shareit.item.service.ItemService;

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

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ItemService service;

    static ItemDto itemDto1;
    static ItemDto itemDto2;

    static ItemInfDto infDto;
    static ItemInfDto infDto2;

    @BeforeAll
    static void beforeAll() {
        itemDto1 = new ItemDto(1L, "Дрель", "Описание тест", true, 1L);
        itemDto2 = new ItemDto(3L, "Пила", "Описание тест", false, 2L);

        ItemInfDto.BookingItemDto bookingLast = new ItemInfDto.BookingItemDto(1L, 1L);
        ItemInfDto.BookingItemDto bookingNext = new ItemInfDto.BookingItemDto(2L, 1L);

        CommentInfDto commentInfDto = new CommentInfDto(1L, "Комментарий", "Иван", LocalDateTime.now());

        infDto = new ItemInfDto(3L, "Кусачки", "Описание тест", true,
                bookingLast, bookingNext, Arrays.asList(commentInfDto));

        infDto2 = new ItemInfDto(4L, "Пила", "Описание тест", true,
                bookingLast, bookingNext, Arrays.asList(commentInfDto));
    }

    @Test
    void shouldEqualsPropertysCreate() throws Exception {

        when(service.create(any(Long.class), any(ItemDto.class)))
                .thenReturn(itemDto1);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.available", is(itemDto1.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto1.getRequestId()), Long.class));

        verify(service, times(1)).create(any(Long.class), any(ItemDto.class));
    }

    @Test
    void shouldReturn4xxCodeWrongUserId() throws Exception {

        when(service.create(any(Long.class), any(ItemDto.class)))
                .thenReturn(itemDto1);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "x")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldVerifyUpdate() throws Exception {

        when(service.update(any(Long.class), any(Long.class), any(ItemDto.class)))
                .thenReturn(itemDto1);

        mockMvc.perform(patch("/items/1")
                .header("X-Sharer-User-Id", 1L)
                .content(mapper.writeValueAsString(itemDto1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        verify(service, times(1)).update(any(Long.class), any(Long.class), any(ItemDto.class));
    }

    @Test
    void shouldEqualsPropertysGetByid() throws Exception {

        when(service.getById(any(Long.class), any(Long.class)))
                .thenReturn(infDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(infDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(infDto.getDescription())))
                .andExpect(jsonPath("$.name", is(infDto.getName())))
                .andExpect(jsonPath("$.available", is(infDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking.id", is(infDto.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.id", is(infDto.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.comments", hasSize(1)));

        verify(service, times(1)).getById(any(Long.class), any(Long.class));
    }

    @Test
    void shouldEqualsSizeGetByOwner() throws Exception {

        List<ItemInfDto> itemInfDtos = Arrays.asList(infDto, infDto2);
        PageRequest request = PageRequest.ofSize(10);

        when(service.getByOwner(1L, request))
                .thenReturn(itemInfDtos);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(service, times(1)).getByOwner(1L, request);
    }

    @Test
    void shouldReturnStatus4xxGetByOwnerInvalidParams() throws Exception {
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-5")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldReturnStatus4xxSearch() throws Exception {
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-5"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldReturnStatusOkAndSizeEquals() throws Exception {

        List<ItemDto> itemDtos = Arrays.asList(itemDto1, itemDto2);
        PageRequest request = PageRequest.ofSize(10);

        when(service.search("дрель", request))
                .thenReturn(itemDtos);

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель")
                        .param("from", "1")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", IsCollectionWithSize.hasSize(2)));
    }

    @Test
    void shouldReturnStatusOkAndEqualsIdAddComment() throws Exception {

        CommentInfDto commentInfDto = new CommentInfDto(1L, "Комментарий", "Иван", LocalDateTime.now());

        when(service.addComment(any(Long.class), any(Long.class), any(CommentDto.class)))
                .thenReturn(commentInfDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(commentInfDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentInfDto.getId()), Long.class));
    }
}