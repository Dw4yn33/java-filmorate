package ru.yandex.practicum.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private static final int DESCRIPTIONMAXLENGTH = 200;

    public static boolean checkForFilmValidation(Film film) {
            if (film.getName().isEmpty() || film.getName().isBlank()){
                String message = "Ошибка: введено пустое название фильма";
                log.info(message);
                throw new ValidationException(message);
            } else if (film.getDescription().length() > DESCRIPTIONMAXLENGTH) {
                String message = "Ошибка: превышена максимально допустимая длина описания фильма";
                log.info(message);
                throw new ValidationException(message);
            } else if (film.getReleaseDate().isBefore(LocalDate.of(1895,12,25))){
                String message = "Ошибка: слишком старый фильм";
                log.info(message);
                throw new ValidationException(message);
            } else if (film.getDuration() <= 0) {
                String message = "Ошибка: продолжительность фильма не может быть отрицательной";
                log.info(message);
                throw new ValidationException(message);
            } else return true;
    }

    @PostMapping
    public void create(@RequestBody Film film) throws ValidationException {
        if (checkForFilmValidation(film)) {
            if (films.containsKey(film.getId())){
                String message = "Ошибка: попытка регистрации нового фильма под чужим идентификатором";
                log.info(message);
                throw new ValidationException(message);
            } else films.put(film.getId(),film);
        }
    }

    @PutMapping
    public void update(@RequestBody Film film) {
        if (checkForFilmValidation(film)) films.put(film.getId(),film);
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

}
