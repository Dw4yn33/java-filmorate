package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class User {

    private final String login;
    private String name;
    private long id;
    private final String email;
    private final LocalDate birthday;
    private Set<Long> friends = new HashSet<>();

    public User(String login, String name, long id, String email, LocalDate date) {
        this.login = login;
        this.name = name;
        this.id = id;
        this.email = email;
        this.birthday = date;
    }

    public void addFriend(Long userId) {
        friends.add(userId);
    }

    public void removeFriend(Long userId) {
        friends.remove(userId);
    }
}
