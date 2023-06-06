package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class User {

    private final String login;
    private String name;
    private int id;
    private final String email;
    private final LocalDate birthday;
}
