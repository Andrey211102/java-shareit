package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {

    public User fromDto(UserDto dtoUser) {

        User user = new User();

        user.setEmail(dtoUser.getEmail());
        user.setName(dtoUser.getName());

        return user;
    }

    public UserDto toDto(User user) {

        UserDto dto = new UserDto();

        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());

        return dto;
    }
}
