package ru.yandex.practicum.jUnitTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    @Test
    public void checkingForEmptyName() {
        Film film = new Film(1," ","norm",
                LocalDate.of(2012,4,25),120);
        ValidationException test = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        FilmController.checkForFilmValidation(film);
                    }
                });
        assertEquals("Ошибка: введено пустое название фильма",test.getMessage());
    }

    @Test
    public void checkingForMoreThan200SymbolsInDescription() {
        Film film = new Film(1,"Avengers","Lorem ipsum dolor sit amet, consectetueradipiscingelit," +
                " sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpatUt wisi enimad" +
                " minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquipex ea commodo" +
                " consequat. Duis autem vel eum iriure dрodspfospdfopsdofpdsofpsdofpsodpfospdofpsdfpsodpfosdop",
                LocalDate.of(2012,4,25),120);
        ValidationException test = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        FilmController.checkForFilmValidation(film);
                    }
                });
        assertEquals("Ошибка: превышена максимально допустимая длина описания фильма",test.getMessage());
    }

    @Test
    public void checkingForTheOldestFilm() {
        Film film = new Film(1,"chicken","norm",
                LocalDate.of(1853,4,25),120);
        ValidationException test = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        FilmController.checkForFilmValidation(film);
                    }
                });
        assertEquals("Ошибка: слишком старый фильм",test.getMessage());
    }

    @Test
    public void checkingForIllegalDuration() {
        Film film = new Film(1,"Japanese War","norm",
                LocalDate.of(1903,4,25),-1);
        ValidationException test = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        FilmController.checkForFilmValidation(film);
                    }
                });
        assertEquals("Ошибка: продолжительность фильма не может быть отрицательной",test.getMessage());
    }
}