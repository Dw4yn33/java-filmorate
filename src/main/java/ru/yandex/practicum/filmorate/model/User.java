package ru.yandex.practicum.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class User {

    private final int id;
    private final String email;
    private final String login;
    private String name;
    private final LocalDate birthday;
}
