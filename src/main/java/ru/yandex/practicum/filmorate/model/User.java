package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;


import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class User {

    private long id;
    @NotNull
    private String login;
    private String name;
    @Email
    @NotNull
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
        if (name == null || name.isBlank() || name.isEmpty()) {
            this.name = login;
        }
    }

    public void addFriend(Long id) {
        friends.add(id);
    }

    public void removeFriend(Long id) {
        friends.remove(id);
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
