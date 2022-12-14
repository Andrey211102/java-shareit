package ru.practicum.shareit.requests.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class ItemRequestInfDto {

    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}


