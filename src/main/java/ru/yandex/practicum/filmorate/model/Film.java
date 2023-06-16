package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
public class Film {

    private long id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final int duration;

    private Set<Long> likes = new HashSet<>();

    public Film(long id, String name, String description, LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
     public void addLike(Long userId) {
        likes.add(userId);
     }

     public void removeLike(Long userId) {
        likes.remove(userId);
     }
}
