package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {


    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Получен запрос GET к эндпоинту /films на получение списка всех фильмов");
        return filmService.getFilms();
    }

    @ResponseBody
    @PostMapping
    public Film create(@Validated @RequestBody Film film) {
        log.info("Получен запрос POST к эндпоинту /films на регистрацию фильма в системе");
        return filmService.create(film);
    }

    @ResponseBody
    @PutMapping
    public Film update(@Validated @RequestBody Film film) {
        log.info("Получен запрос PUT к эндпоинту /films на обновление информации о фильме в системе");
        return filmService.update(film);
    }

    @GetMapping("/{id}")
    public Film getFilmByIndex(@PathVariable Long id) {
        log.info("Получен запрос GET к эндпоинту /films/" + id + " на получение фильма с идентификатором " + id);
        return filmService.getFilmById(id);
    }

    @DeleteMapping("/{id}")
    public Film deleteFilmByIndex(@PathVariable Long id) {
        log.info("Получен запрос DELETE к эндпоинту /films/" + id + " на удаление фильма с идентификатором " + id);
        return filmService.deleteFilmById(id);
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
