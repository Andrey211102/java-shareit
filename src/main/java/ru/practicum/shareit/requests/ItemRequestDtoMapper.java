package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDtoMapper;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestInfDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestDtoMapper {

    private final ItemDtoMapper itemDtoMapper;

    public ItemRequest fromDto(ItemRequestDto dto, User user) {

        ItemRequest request = new ItemRequest();

        request.setCreated(LocalDateTime.now());
        request.setUser(user);
        request.setDescription(dto.getDescription());

        return request;
    }

    public ItemRequestInfDto toInfDto(ItemRequest request, List<Item> items) {

        ItemRequestInfDto infDto = new ItemRequestInfDto();

        infDto.setId(request.getId());
        infDto.setCreated(request.getCreated());
        infDto.setDescription(request.getDescription());
        infDto.setItems(items.stream()
                .map(item -> itemDtoMapper.toDto(item))
                .collect(Collectors.toList()));

        return infDto;
    }
}
