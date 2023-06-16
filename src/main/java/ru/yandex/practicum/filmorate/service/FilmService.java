package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmComparator;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
@Component
public class FilmService {

    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(long filmId, long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        if (film != null) {
            if (user != null) {
                film.addLike(user.getId());
            } else throw new UserNotFoundException("Несуществующий пользователь не может поставить лайк");
        } else throw new FilmNotFoundException("Фильм, которому пытаются поставить лайк, не найден");
    }

    public void removeLike(long filmId, long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        if (film != null) {
            if (user != null) {
                film.removeLike(user.getId());
            } else throw new UserNotFoundException("Несуществующий пользователь не может удалить лайк");
        } else throw new FilmNotFoundException("Фильм, у которого пытаются удалить лайк, не найден");
    }

    public List<Film> getPopular(Integer count) {
        if (count < 1) {
            throw new ValidationException("Кол-во фильмов не может быть меньше 1");
        }
        Set<Film> topLikedFilms = new TreeSet<>(new FilmComparator());
        topLikedFilms.addAll(filmStorage.getFilms());
        List<Film> listTop = new ArrayList<>(topLikedFilms);
        List<Film> finalTop = new ArrayList<>();
        if (count > listTop.size()) {
            count = listTop.size() + 1;
        }
        for (int i = 0; i < count; i++) {
            finalTop.add(listTop.get(i));
        }
        return finalTop;
    }
}
