package ru.practicum.shareit.requests;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestInfDto;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService service;

    @Autowired
    private MockMvc mockMvc;

    static ItemRequestInfDto infDto1;
    static ItemRequestInfDto infDto2;

    @BeforeAll
    static void beforeAll() {
        User user1 = new User(1L, "testUser1", "user1@mail.test");
        User user2 = new User(2L, "testUser2", "user2@mail.test");

        ItemRequest request1 = new ItemRequest(1L, "Тестовое описание", LocalDateTime.now(), user1);
        ItemRequest request2 = new ItemRequest(2L, "Тестовое описание", LocalDateTime.now(), user2);

        ItemDto itemDto1 = new ItemDto(1L, "Дрель", "Описание тест", true, 1L);
        ItemDto itemDto2 = new ItemDto(2L, "Пила", "Описание тест", false, 1L);
        ItemDto itemDto3 = new ItemDto(3L, "Ножовка", "Описание тест", false, 2L);

        infDto1 = new ItemRequestInfDto(request1.getId(), request1.getDescription(), request1.getCreated(),
                Arrays.asList(itemDto1, itemDto2));

        infDto2 = new ItemRequestInfDto(request2.getId(), request2.getDescription(), request2.getCreated(),
                Arrays.asList(itemDto3));
    }

    @Test
    void shouldEqualsPropertysCreate() throws Exception {

        when(service.create(any(Long.class), any(ItemRequestDto.class)))
                .thenReturn(infDto1);

        ItemDto itemDto = infDto1.getItems().get(0);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(infDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(infDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(infDto1.getDescription())))
                .andExpect(jsonPath("$.created").isNotEmpty())

                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.items[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.items[0].available", is(itemDto.getAvailable())));

        verify(service, times(1)).create(any(Long.class), any(ItemRequestDto.class));
    }

    @Test
    void shouldEqualsPropertusGetByid() throws Exception {

        when(service.getById(any(Long.class), any(Long.class)))
                .thenReturn(infDto1);

        ItemDto itemDto = infDto1.getItems().get(0);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(infDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(infDto1.getDescription())))
                .andExpect(jsonPath("$.created").isNotEmpty())

                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.items[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.items[0].available", is(itemDto.getAvailable())));

        verify(service, times(1)).getById(any(Long.class), any(Long.class));
    }

    @Test
    void shouldEqualsSizeAndStatusOkGetByUserid() throws Exception {

        PageRequest request = PageRequest.ofSize(10);

        when(service.getByUserid(2L, request))
                .thenReturn(Arrays.asList(infDto1, infDto2));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 2L)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", IsCollectionWithSize.hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(infDto1.getId()), Long.class));

        verify(service, times(1)).getByUserid(2L, request);
    }


    @Test
    void shouldEqualsCountRequestsAndStatusOkGetAll() throws Exception {

        when(service.getAll(any(), eq(PageRequest.of(0, 10))))
                .thenReturn(Arrays.asList(infDto1, infDto2));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", IsCollectionWithSize.hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(infDto1.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(infDto2.getId()), Long.class));
        ;

        verify(service, times(1)).getAll(1L, PageRequest.of(0, 10));
    }
}