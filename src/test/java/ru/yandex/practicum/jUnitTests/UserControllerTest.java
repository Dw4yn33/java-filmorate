package ru.yandex.practicum.jUnitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    InMemoryUserStorage storage;
    Set<Long> friends;

    @BeforeEach
    public void createValidator() {
        storage = new InMemoryUserStorage();
        friends = new HashSet<>();
    }

    @Test
    public void checkingForEmptyEmail() {
        User user = new User(1,"abobus228","Anatoliy","",
                LocalDate.of(1999,11,11),friends);
        ValidationException test = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        storage.checkForUserValidation(user);
                    }
                });
        assertEquals("Ошибка: введенная почта пуста",test.getMessage());
    }

    @Test
    public void checkingForSpaceBarsInEmail() {
        User user = new User(1,"abobus228","Anatoliy", "anatoliy @mail.ru",
                LocalDate.of(1999,11,11),friends);
        ValidationException test = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        storage.checkForUserValidation(user);
                    }
                });
        assertEquals("Ошибка: в введенной почте присутствуют пробелы",test.getMessage());
    }

    @Test
    public void checkingForSpecialSymbolInEmail() {
        User user = new User(1,"abobus228","Anatoliy","bombavdomemail.ru",
                LocalDate.of(1999,11,11),friends);
        ValidationException test = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        storage.checkForUserValidation(user);
                    }
                });
        assertEquals("Ошибка: то, что вы ввели - не почта",test.getMessage());
    }

    @Test
    public void checkingForEmptyLogin() {
        User user = new User(1,"","Anatoliy","anatoliy@mail.ru",
                LocalDate.of(1999,11,11),friends);
        ValidationException test = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        storage.checkForUserValidation(user);
                    }
                });
        assertEquals("Ошибка: пустой логин",test.getMessage());
    }

    @Test
    public void checkingForSpaceBarsInLogin() {
        User user = new User(1,"abobus 228","Anatoliy","anatoliy@mail.ru",
                LocalDate.of(1999,11,11),friends);
        ValidationException test = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        storage.checkForUserValidation(user);
                    }
                });
        assertEquals("Ошибка: в логине присутствуют пробелы",test.getMessage());
    }

    @Test
    public void checkingForCorrectBirthDate() {
        User user = new User(1,"abobus228","Anatoliy","anatoliy@mail.ru",
                LocalDate.of(2023,11,11),friends);
        ValidationException test = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        storage.checkForUserValidation(user);
                    }
                });
        assertEquals("Ошибка: пользователь не может быть из будущего",test.getMessage());
    }

    @Test
    public void checkingForMakingNameFromLogin() {
        User user = new User(1,"abobus228","","anatoliy@mail.ru",
                LocalDate.of(2022,11,11),friends);
        storage.checkForUserValidation(user);
        assertEquals(user.getName(),user.getLogin());
    }
}