package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private static final int DESCRIPTIONMAXLENGTH = 200;
    private final JdbcTemplate jdbcTemplate;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final LikeStorage likeStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaService mpaService, GenreService genreService,
                         LikeStorage likeStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaService = mpaService;
        this.genreService = genreService;
        this.likeStorage = likeStorage;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_Date").toLocalDate(),
                rs.getInt("duration"),
                new HashSet<>(likeStorage.getLikes(rs.getLong("id"))),
                mpaService.getMpaById(rs.getInt("rating_id")),
                genreService.getFilmGenres(rs.getLong("id")))
        );

    }

    @Override
    public Film create(Film film) {
        checkForFilmValidation(film);
        if (isThatFilmDuplicatedPost(film)) {
            throw new ValidationException("Ошибка: попытка сохранения дубликата фильма");
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue());
        film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                genre.setName(genreService.getGenreById(genre.getId()).getName());
            }
            genreService.putGenres(film);
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        checkForFilmValidation(film);
        if (getFilmById(film.getId()) != null) {
            if (isThatFilmDuplicatedPut(film)) {
                throw new ValidationException("Ошибка: попытка сохранения дубликата фильма");
            }
            String sqlQuery = "UPDATE films SET " +
                    "name = ?, description = ?, release_date = ?, duration = ?, " +
                    "rating_id = ? WHERE id = ?";
            if (jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId()) != 0) {
                film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
                if (film.getGenres() != null) {
                    Collection<Genre> sortGenres = film.getGenres().stream()
                            .sorted(Comparator.comparing(Genre::getId))
                            .collect(Collectors.toList());
                    film.setGenres(new LinkedHashSet<>(sortGenres));
                    for (Genre genre : film.getGenres()) {
                        genre.setName(genreService.getGenreById(genre.getId()).getName());
                    }
                }
                genreService.putGenres(film);
            }
            return film;
        } else {
            throw new FilmNotFoundException("Фильм с ID=" + film.getId() + " не найден!");
        }
    }

    @Override
    public Film getFilmById(Long filmId) {
        if (filmId == null) {
            throw new ValidationException("Передан пустой аргумент!");
        }
        Film film;
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE id = ?", filmId);
        if (filmRows.first()) {
            Mpa mpa = mpaService.getMpaById(filmRows.getInt("rating_id"));
            Set<Genre> genres = genreService.getFilmGenres(filmId);
            film = new Film(
                    filmRows.getLong("id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getInt("duration"),
                    new HashSet<>(likeStorage.getLikes(filmRows.getLong("id"))),
                    mpa,
                    genres);
        } else {
            throw new FilmNotFoundException("Фильм с ID=" + filmId + " не найден!");
        }
        if (film.getGenres().isEmpty()) {
            film.setGenres(new HashSet<>());
        }
        return film;
    }

    @Override
    public Film deleteFilmById(Long filmId) {
        if (filmId == null) {
            throw new ValidationException("Передан пустой аргумент!");
        }
        Film film = getFilmById(filmId);
        String sqlQuery = "DELETE FROM films WHERE id = ? ";
        if (jdbcTemplate.update(sqlQuery, filmId) == 0) {
            throw new FilmNotFoundException("Фильм с ID=" + filmId + " не найден!");
        }
        return film;
    }

    @Override
    public boolean checkForFilmValidation(Film film) {
        if (film.getName() == null || film.getName().isEmpty() || film.getName().isBlank()) {
            throw new ValidationException("Ошибка: введено пустое название фильма");
        } else if (film.getDescription().length() > DESCRIPTIONMAXLENGTH || film.getDescription().length() < 1) {
            throw new ValidationException("Ошибка: превышена максимально допустимая длина описания фильма");
        } else if (film.getReleaseDate() == null) {
            throw new ValidationException("Ошибка: фильм с неизвестной датой выпуска");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895,12,25))) {
            throw new ValidationException("Ошибка: слишком старый фильм");
        } else if (film.getDuration() == null) {
            throw new ValidationException("Ошибка: у фильма отсутствует продолжительность");
        } else if (film.getDuration() <= 0) {
            throw new ValidationException("Ошибка: продолжительность фильма не может быть отрицательной");
        } else if (film.getMpa() == null) {
            throw new ValidationException("Ошибка: у фильма отсутствует рейтинг");
        } else if (film.getGenres() != null) {
            if (!film.getGenres().isEmpty()) {
                for (Genre genre : film.getGenres()) {
                    if (genre.getId() == null) {
                        throw new ValidationException("Ошибка: у жанров фильма отсутствует идентификатор");
                    }
                }
            }
        }
        return true;
    }

    public boolean isThatFilmDuplicatedPost(Film film) {
        if (getFilms() != null) {
            for (Film check : getFilms()) {
                if (film.getId() != null && check.getId() != null) {
                    if (film.getId().equals(check.getId())) continue;
                }
                if (film.getName().equals(check.getName()) && film.getDescription().equals(check.getDescription())
                        && film.getDuration().equals(check.getDuration()) && film.getReleaseDate().equals(check.getReleaseDate())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isThatFilmDuplicatedPut(Film film) {
        if (getFilms() != null) {
            for (Film check : getFilms()) {
                if (film.getId().equals(check.getId())) continue;
                if (film.getName().equals(check.getName()) && film.getDescription().equals(check.getDescription())
                        && film.getDuration().equals(check.getDuration()) && film.getReleaseDate().equals(check.getReleaseDate())) {
                    return true;
                }
            }
        }
        return false;
    }
}
