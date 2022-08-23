package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.user.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {

    private Long id;
    @NotBlank(groups = {Create.class})
    private String name;
    @NotBlank(groups = {Create.class})
    @Email(groups = {Create.class})
    private String email;
}
