package ru.yandex.practicum.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.exceptions.ValidationException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer,User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);


    public static boolean checkForUserValidation(User user) {
            if (user.getEmail().isEmpty()) {
                String message = "Ошибка: введенная почта пуста";
                log.info(message);
                throw new ValidationException(message);
            } else if (user.getEmail().contains(" ")){
                String message = "Ошибка: в введенной почте присутствуют пробелы";
                log.info(message);
                throw new ValidationException(message);
            } else if (!user.getEmail().contains("@")) {
                String message = "Ошибка: то, что вы ввели - не почта";
                log.info(message);
                throw new ValidationException(message);
            } else if (user.getLogin().isEmpty()) {
                String message = "Ошибка: пустой логин";
                log.info(message);
                throw new ValidationException(message);
            } else if (user.getLogin().contains(" ")){
                String message = "Ошибка: в логине присутствуют пробелы";
                log.info(message);
                throw new ValidationException(message);
            } else if (user.getBirthday().isAfter(LocalDate.now())) {
                String message = "Ошибка: пользователь не может быть из будущего";
                log.info(message);
                throw new ValidationException(message);
            } else if (user.getName().isEmpty() || user.getName().isBlank()) {
                user.setName(user.getLogin());
                return true;
            } else return true;
    }
    @PostMapping
    public void create(@RequestBody User user) throws ValidationException {
        if (checkForUserValidation(user)) {
            if (users.containsKey(user.getId())){
                String message = "Ошибка: попытка регистрации нового пользователя под чужим иденитфикатором";
                log.info(message);
                throw new ValidationException(message);
            } else {
                users.put(user.getId(),user);
            }
        }
    }

    @PutMapping
    public void update(@RequestBody User user) {
        if (checkForUserValidation(user)) users.put(user.getId(), user);
    }

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

}
