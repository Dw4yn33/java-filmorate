package ru.yandex.practicum.filmorate.storage.film;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private static final int DESCRIPTIONMAXLENGTH = 200;
    private Long generatorId;
    private Map<Long,Film> films;

    public InMemoryFilmStorage() {
        generatorId = 1L;
        films = new HashMap<>();
    }

    @Override
    public Film create(Film film) {
        if (checkForFilmValidation(film)) {
            if (films.containsKey(film.getId())) {
                throw new ValidationException("Ошибка: попытка регистрации нового фильма под чужим идентификатором");
            }
            films.put(film.getId(), film);
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        if (checkForFilmValidation(film)) {
            if (films.containsKey(film.getId())) {
                films.put(film.getId(),film);
            } else throw new FilmNotFoundException("Фильм с идентификатором " + film.getId() +
                    " не был добавлен" + ", нечего обновлять");
        }
        return film;
    }

    @Override
    public Film getFilmById(Long filmId) {
        if (filmId == null) {
            throw new ValidationException("Передан пустой идентификатор");
        }
        if (!films.containsKey(filmId)) {
            throw new FilmNotFoundException("Фильм с идентификатором " + filmId + " не найден");
        }
        return films.get(filmId);
    }

    public Film deleteFilmById(Long filmId) {
        if (filmId == null) {
            throw new ValidationException("Передан пустой идентификатор");
        }
        if (!films.containsKey(filmId)) {
            throw new FilmNotFoundException("Фильм с идентификатором " + filmId + " не найден");
        }
        return films.remove(filmId);
    }

    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    public boolean checkForFilmValidation(Film film) {
        if (film.getName().isEmpty() || film.getName().isBlank()) {
            throw new ValidationException("Ошибка: введено пустое название фильма");
        } else if (film.getDescription().length() > DESCRIPTIONMAXLENGTH) {
            throw new ValidationException("Ошибка: превышена максимально допустимая длина описания фильма");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895,12,25))) {
            throw new ValidationException("Ошибка: слишком старый фильм");
        } else if (film.getDuration() <= 0) {
            throw new ValidationException("Ошибка: продолжительность фильма не может быть отрицательной");
        }
        if (film.getId() <= 0) {
            film.setId(generatorId++);
        }
        return true;
    }
}
