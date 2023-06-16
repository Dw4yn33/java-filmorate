package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {


    private FilmStorage filmStorage;
    private FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Получен запрос GET к эндпоинту /films на получение списка всех фильмов");
        return filmStorage.getFilms();
    }

    @ResponseBody
    @PostMapping
    public Film create(@Validated @RequestBody Film film) {
        log.info("Получен запрос POST к эндпоинту /films на регистрацию фильма в системе");
        return filmStorage.create(film);
    }

    @ResponseBody
    @PutMapping
    public Film update(@Validated @RequestBody Film film) {
        log.info("Получен запрос PUT к эндпоинту /films на обновление информации о фильме в системе");
        return filmStorage.update(film);
    }

    @GetMapping("/{id}")
    public Film getFilmByIndex(@PathVariable Long id) {
        log.info("Получен запрос GET к эндпоинту /films/" + id + " на получение фильма с идентификатором " + id);
        return filmStorage.getFilmById(id);
    }

    @DeleteMapping("/{id}")
    public Film deleteFilmByIndex(@PathVariable Long id) {
        log.info("Получен запрос DELETE к эндпоинту /films/" + id + " на удаление фильма с идентификатором " + id);
        return filmStorage.deleteFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(name = "count",defaultValue = "10") Integer count) {
        log.info("Получен запрос GET к эндпоинту /films/popular на получение списка из " + count +
                " самых популярных фильмов");
        return filmService.getPopular(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос PUT на добавление лайка пользователем с идентификатором " + userId + " фильму с " +
                "идентификатором " + id);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос DELETE на удаление лайка пользователем с идентификатором " + userId + " фильму с " +
                "идентификатором " + id);
        filmService.removeLike(id, userId);
    }
}
