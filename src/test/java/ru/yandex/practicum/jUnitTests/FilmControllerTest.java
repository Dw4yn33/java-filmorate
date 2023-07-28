package ru.yandex.practicum.jUnitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    InMemoryFilmStorage storage;
    Set<Long> likes;
    Mpa mpa;
    Set<Genre> genres;

    @BeforeEach
    public void createValidator() {
        storage = new InMemoryFilmStorage();
        likes = new HashSet<>();
        mpa = new Mpa(1,"che-ta");
        Genre genre = new Genre(1,"Isekai");
        genres = new HashSet<>();
        genres.add(genre);
    }

    @Test
    public void checkingForEmptyName() {
        Film film = new Film(1L," ","norm",
                LocalDate.of(2012,4,25),120, likes, mpa, genres);
        ValidationException test = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        storage.checkForFilmValidation(film);
                    }
                });
        assertEquals("Ошибка: введено пустое название фильма",test.getMessage());
    }

    @Test
    public void checkingForMoreThan200SymbolsInDescription() {
        Film film = new Film(1L,"Avengers","Lorem ipsum dolor sit amet, consectetueradipiscingelit," +
                " sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpatUt wisi enimad" +
                " minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquipex ea commodo" +
                " consequat. Duis autem vel eum iriure dрodspfospdfopsdofpdsofpsdofpsodpfospdofpsdfpsodpfosdop",
                LocalDate.of(2012,4,25),120, likes, mpa, genres);
        ValidationException test = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        storage.checkForFilmValidation(film);
                    }
                });
        assertEquals("Ошибка: превышена максимально допустимая длина описания фильма",test.getMessage());
    }

    @Test
    public void checkingForTheOldestFilm() {
        Film film = new Film(1L,"chicken","norm",
                LocalDate.of(1853,4,25),120, likes, mpa, genres);
        ValidationException test = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        storage.checkForFilmValidation(film);
                    }
                });
        assertEquals("Ошибка: слишком старый фильм",test.getMessage());
    }

    @Test
    public void checkingForIllegalDuration() {
        Film film = new Film(1L,"Japanese War","norm",
                LocalDate.of(1903,4,25),-1, likes, mpa, genres);
        ValidationException test = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        storage.checkForFilmValidation(film);
                    }
                });
        assertEquals("Ошибка: продолжительность фильма не может быть отрицательной",test.getMessage());
    }
}