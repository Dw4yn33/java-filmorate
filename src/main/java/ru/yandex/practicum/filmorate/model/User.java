package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class User {

    private long id;
    private String login;
    private String name;
    private String email;
    private LocalDate birthday;
    private Set<Long> friends;

    public User(long id, String login, String name, String email, LocalDate date, Set<Long> friends) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.email = email;
        this.birthday = date;
        this.friends = friends;
        if (friends == null) {
            this.friends = new HashSet<>();
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("login", login);
        values.put("name", name);
        values.put("email", email);
        values.put("birthday", birthday);
        return values;
    }
}
