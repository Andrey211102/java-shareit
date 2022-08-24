package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.Create;
import ru.practicum.shareit.user.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService service;

    @GetMapping
    public List<UserDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        return service.getById(id);
    }

    @PostMapping
    public UserDto create(@Validated({Create.class}) @RequestBody UserDto userDto) {
        return service.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @Validated({Update.class}) @RequestBody UserDto userDto) {
        return service.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        service.deleteById(id);
    }
}
