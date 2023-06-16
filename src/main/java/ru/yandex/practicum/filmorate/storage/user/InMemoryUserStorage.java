package ru.yandex.practicum.filmorate.storage.user;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private Map<Long,User> users;
    private Long generatorId;

    public InMemoryUserStorage() {
        users = new HashMap<>();
        generatorId = 1L;
    }

    @Override
    public User create(User user) {
        if (checkForUserValidation(user)) {
            if (users.containsKey(user.getId())) {
                String message = "Ошибка: попытка регистрации нового пользователя под чужим идентификатором";
                throw new ValidationException(message);
            }
            users.put(user.getId(),user);
        }
        return user;
    }

    @Override
    public User update(User user) {
        if (checkForUserValidation(user)) {
            if (users.containsKey(user.getId())) {
                users.put(user.getId(), user);
            } else throw new UserNotFoundException("Пользователь с идентификатором" + user.getId() +
                    "не был зарегистрирован" + ", нечего обновлять");
        }
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        if (userId == null) {
            throw new ValidationException("Передан пустой идентификатор");
        }
        if (!users.containsKey(userId)) {
            throw new FilmNotFoundException("Фильм с идентификатором " + userId + " не найден");
        }
        return users.get(userId);
    }

    @Override
    public User deleteUserById(Long userId) {
        if (userId == null) {
            throw new ValidationException("Передан пустой идентификатор");
        }
        if (!users.containsKey(userId)) {
            throw new FilmNotFoundException("Фильм с идентификатором " + userId + " не найден");
        }
        return users.remove(userId);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }


    public boolean checkForUserValidation(User user) {
        if (user.getEmail().isEmpty()) {
            throw new ValidationException("Ошибка: введенная почта пуста");
        } else if (user.getEmail().contains(" ")) {
            throw new ValidationException("Ошибка: в введенной почте присутствуют пробелы");
        } else if (!user.getEmail().contains("@")) {
            throw new ValidationException("Ошибка: то, что вы ввели - не почта");
        } else if (user.getLogin().isEmpty()) {
            throw new ValidationException("Ошибка: пустой логин");
        } else if (user.getLogin().contains(" ")) {
            throw new ValidationException("Ошибка: в логине присутствуют пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Ошибка: пользователь не может быть из будущего");
        }
        if (user.getId() <= 0) {
            user.setId(generatorId);
            generatorId++;
        }
        if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return true;
    }
}
