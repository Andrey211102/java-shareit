package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class CommentDto {

    @NotBlank(groups = {Create.class})
    private String text;
}
