package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemDtoMapper {

    public Item fromDto(ItemDto dtoItem) {

        Item item = new Item();

        item.setName(dtoItem.getName());
        item.setDescription(dtoItem.getDescription());
        item.setAvailable(dtoItem.getAvailable());

        return item;
    }

    public ItemDto toDto(Item item) {

        ItemDto dto = new ItemDto();

        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.isAvailable());

        return dto;
    }

}
