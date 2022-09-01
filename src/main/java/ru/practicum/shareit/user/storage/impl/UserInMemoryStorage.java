package ru.practicum.shareit.user.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserInMemoryStorage implements UserStorage {

    private final HashMap<Long, User> users;
    private final HashMap<String, User> emails;

    private Long lastUid = 1L;

    @Override
    public User create(User newUser) {

        newUser.setId(lastUid);
        users.put(lastUid, newUser);
        emails.put(newUser.getEmail(), newUser);

        setLastUid();

        return newUser;
    }

    @Override
    public User update(long id, UserDto userDto) {

        User oldUser = users.get(id);

        if (userDto.getName() != null) oldUser.setName(userDto.getName());

        if (userDto.getEmail() != null) {
            emails.remove(oldUser.getEmail());
            oldUser.setEmail(userDto.getEmail());
            emails.put(oldUser.getEmail(), oldUser);
        }
        users.put(id, oldUser);

        return oldUser;
    }

    @Override
    public User getById(long id) {
        return users.get(id);
    }

    @Override
    public void delete(long id) {
        emails.remove(users.get(id).getEmail());
        users.remove(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    public boolean emailIsBusy(String email) {
        return emails.containsKey(email);
    }

    public User userByMail(String email) {
        return emails.get(email);
    }

    private void setLastUid() {
        if (this.users.isEmpty()) this.lastUid = 1L;
        else this.lastUid++;
    }
}
