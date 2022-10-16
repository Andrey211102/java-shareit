package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserServiceImpl service;

    @Autowired
    private MockMvc mockMvc;

    UserDto userDto1;
    UserDto userDto2;
    UserDto userDto3;

    @BeforeEach
    void beforeEach() {
        userDto1 = new UserDto(1L, "user1", "tes1@mail.com");
        userDto2 = new UserDto(2L, "user2", "tes2@mail.com");
        userDto3 = new UserDto(3L, "user3", "tes3@mail.com");
    }

    @Test
    void shouldEqualsCreateNewUser() throws Exception {
        when(service.create(any()))
                .thenReturn(userDto1);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));
    }

    @Test
    void shouldEqualsGetAllUsersSize() throws Exception {

        List<UserDto> dtos = new ArrayList<>();
        dtos.add(userDto1);
        dtos.add(userDto2);
        dtos.add(userDto3);

        when(service.getAll())
                .thenReturn(dtos);

        mockMvc.perform(get("/users")
                        .content(mapper.writeValueAsString(dtos))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void shouldEqualsGetAllUsers() throws Exception {

        List<UserDto> dtos = new ArrayList<>();
        dtos.add(userDto1);
        dtos.add(userDto2);
        dtos.add(userDto3);

        when(service.getAll()).thenReturn(dtos);

        mockMvc.perform(get("/users")
                        .content(mapper.writeValueAsString(dtos))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[1].id", is(userDto2.getId()), Long.class))
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(userDto1, userDto2, userDto3))))
                .andExpect(jsonPath("$[1].name", is(userDto2.getName())))
                .andExpect(jsonPath("$[1].email", is(userDto2.getEmail())));
    }

    @Test
    void shouldEqualsGetById() throws Exception {

        when(service.getById(1)).thenReturn(userDto1);

        mockMvc.perform(get("/users/1")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));
    }

    @Test
    void shouldReturnStatusOkPatchUpdateUser() throws Exception {

        when(service.update(1, userDto2))
                .thenReturn(userDto2);

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnStatusOkByDeleteAndCallOnceService() throws Exception {

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andReturn();

        verify(service, times(1)).deleteById(1L);
    }
}