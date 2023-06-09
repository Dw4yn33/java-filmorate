package ru.yandex.practicum.jUnitTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    @Test
    public void checkingForEmptyEmail() {
        User user = new User("abobus228","Anatoliy",1,"",
                LocalDate.of(1999,11,11));
        ValidationException test = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        UserController.checkForUserValidation(user);
                    }
                });
        assertEquals("Ошибка: введенная почта пуста",test.getMessage());
    }

    @Test
    public void checkingForSpaceBarsInEmail() {
        User user = new User("abobus228","Anatoliy",1, "anatoliy @mail.ru",
                LocalDate.of(1999,11,11));
        ValidationException test = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        UserController.checkForUserValidation(user);
                    }
                });
        assertEquals("Ошибка: в введенной почте присутствуют пробелы",test.getMessage());
    }

    @Test
    public void checkingForSpecialSymbolInEmail() {
        User user = new User("abobus228","Anatoliy",1,"bombavdomemail.ru",
                LocalDate.of(1999,11,11));
        ValidationException test = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        UserController.checkForUserValidation(user);
                    }
                });
        assertEquals("Ошибка: то, что вы ввели - не почта",test.getMessage());
    }

    @Test
    public void checkingForEmptyLogin() {
        User user = new User("","Anatoliy",1,"anatoliy@mail.ru",
                LocalDate.of(1999,11,11));
        ValidationException test = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        UserController.checkForUserValidation(user);
                    }
                });
        assertEquals("Ошибка: пустой логин",test.getMessage());
    }

    @Test
    public void checkingForSpaceBarsInLogin() {
        User user = new User("abobus 228","Anatoliy",1,"anatoliy@mail.ru",
                LocalDate.of(1999,11,11));
        ValidationException test = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        UserController.checkForUserValidation(user);
                    }
                });
        assertEquals("Ошибка: в логине присутствуют пробелы",test.getMessage());
    }

    @Test
    public void checkingForCorrectBirthDate() {
        User user = new User("abobus228","Anatoliy",1,"anatoliy@mail.ru",
                LocalDate.of(2023,11,11));
        ValidationException test = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        UserController.checkForUserValidation(user);
                    }
                });
        assertEquals("Ошибка: пользователь не может быть из будущего",test.getMessage());
    }

    @Test
    public void checkingForMakingNameFromLogin() {
        User user = new User("abobus228","",1,"anatoliy@mail.ru",
                LocalDate.of(2022,11,11));
        UserController.checkForUserValidation(user);
        assertEquals(user.getName(),user.getLogin());
    }
}