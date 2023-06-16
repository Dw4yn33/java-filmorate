package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class FilmComparator implements Comparator<Film> {

    public int compare(Film film1,Film film2) {
        if (film1.getLikes().size() > film2.getLikes().size()) {
            return -1;
        } else if (film1.getLikes().size() < film2.getLikes().size()) {
            return 1;
        } else if (film1.getId() < film2.getId()) {
            return -1;
        } else if (film1.getId() > film2.getId()) {
            return 1;
        } else return 0;
    }
}
