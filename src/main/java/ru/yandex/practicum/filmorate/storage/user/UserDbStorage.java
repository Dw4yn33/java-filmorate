package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getDate("birthday").toLocalDate(),
                null)
        );
    }

    @Override
    public User create(User user) {
        checkForUserValidation(user);
        if (isThatUserDuplicatedPost(user)) {
            throw new ValidationException("Ошибка: попытка сохранения дубликата пользователя");
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue());

        log.info("Добавлен новый пользователь с ID={}", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        checkForUserValidation(user);
        if (getUserById(user.getId()) != null) {
            if (isThatUserDuplicatedPut(user)) {
                throw new ValidationException("Ошибка: попытка сохранения дубликата пользователя");
            }
            String sqlQuery = "UPDATE users SET " +
                    "email = ?, login = ?, name = ?, birthday = ? " +
                    "WHERE id = ?";
            jdbcTemplate.update(sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
            log.info("Пользователь с ID={} успешно обновлен", user.getId());
            return user;
        } else {
            throw new UserNotFoundException("Пользователь с ID=" + user.getId() + " не найден!");
        }
    }

    @Override
    public User getUserById(Long userId) {
        if (userId == null) {
            throw new ValidationException("Ошибка: передан пустой аргумент!");
        }
        User user;
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ?", userId);
        if (userRows.first()) {
            user = new User(
                    userRows.getLong("id"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getString("email"),
                    userRows.getDate("birthday").toLocalDate(),
                    null);
        } else {
            throw new UserNotFoundException("Пользователь с ID=" + userId + " не найден!");
        }
        return user;
    }

    @Override
    public User deleteUserById(Long userId) {
        if (userId == null) {
            throw new ValidationException("Ошибка: передан пустой аргумент!");
        }
        User user = getUserById(userId);
        String sqlQuery = "DELETE FROM users WHERE id = ? ";
        if (jdbcTemplate.update(sqlQuery, userId) == 0) {
            throw new UserNotFoundException("Пользователь с ID=" + userId + " не найден!");
        }
        return user;
    }



    @Override
    public boolean checkForUserValidation(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new ValidationException("Ошибка: введенная почта пуста");
        } else if (user.getEmail().contains(" ")) {
            throw new ValidationException("Ошибка: в введенной почте присутствуют пробелы");
        } else if (!user.getEmail().contains("@")) {
            throw new ValidationException("Ошибка: то, что вы ввели - не почта");
        } else if (user.getLogin() == null || user.getLogin().isEmpty()) {
            throw new ValidationException("Ошибка: пустой логин");
        } else if (user.getLogin().contains(" ")) {
            throw new ValidationException("Ошибка: в логине присутствуют пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Ошибка: пользователь не может быть из будущего");
        }
        return true;
    }

    public boolean isThatUserDuplicatedPost(User user) {
        if (getUsers() != null) {
            for (User check : getUsers()) {
                if (user.getLogin().equals(check.getLogin()) && user.getEmail().equals(check.getEmail())
                        && user.getName().equals(check.getName()) && user.getBirthday().equals(check.getBirthday())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isThatUserDuplicatedPut(User user) {
        if (getUsers() != null) {
            for (User check : getUsers()) {
                if (user.getId() == check.getId()) continue;
                if (user.getLogin().equals(check.getLogin()) && user.getEmail().equals(check.getEmail())
                        && user.getName().equals(check.getName()) && user.getBirthday().equals(check.getBirthday())) {
                    return true;
                }
            }
        }
        return false;
    }
}
