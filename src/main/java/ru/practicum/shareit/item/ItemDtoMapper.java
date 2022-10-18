package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfDto;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemDtoMapper {

    private final CommentDtoMapper commentMapper;

    public Item fromDto(ItemDto dtoItem, User owner, Optional<ItemRequest> request) {

        Item item = new Item();

        item.setName(dtoItem.getName());
        item.setDescription(dtoItem.getDescription());
        item.setAvailable(dtoItem.getAvailable());
        item.setOwner(owner);
        if (request.isPresent()) item.setRequest(request.get());

        return item;
    }

    public ItemDto toDto(Item item) {

        ItemDto dto = new ItemDto();

        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.isAvailable());
        if (item.getRequest() != null) dto.setRequestId(item.getRequest().getId());

        return dto;
    }

    public ItemInfDto toInfDto(Item item, Optional<Booking> lastBooking, Optional<Booking> nextBooking,
                               List<Comment> comments) {

        ItemInfDto dto = new ItemInfDto();

        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.isAvailable());

        dto.setComments(comments.stream()
                .map(comment -> commentMapper.toInfDto(comment))
                .collect(Collectors.toList()));

        if (lastBooking.isPresent()) dto.setLastBooking(new ItemInfDto.BookingItemDto(lastBooking.get().getId(),
                lastBooking.get().getBooker().getId()));

        if (nextBooking.isPresent()) dto.setNextBooking(new ItemInfDto.BookingItemDto(nextBooking.get().getId(),
                nextBooking.get().getBooker().getId()));

        return dto;
    }
}
