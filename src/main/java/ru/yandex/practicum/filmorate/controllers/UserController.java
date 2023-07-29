package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получен запрос GET к эндпоинту /users на получение списка всех пользователей");
        return userService.getUsers();
    }

    @ResponseBody
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Получен запрос POST к эндпоинту /users на регистрацию пользователя в системе");
        return userService.create(user);
    }

    @ResponseBody
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Получен запрос PUT к эндпоинту /users на обновление информации о пользователе в системе");
        return userService.update(user);
    }

    @GetMapping("/{id}")
    public User getUserByIndex(@PathVariable Long id) {
        log.info("Получен запрос GET к эндпоинту /users/" + id + " на получение пользователя с идентификатором " + id);
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public User deleteUserByIndex(@PathVariable Long id) {
        log.info("Получен запрос DELETE к эндпоинту /users/" + id + " на удаление пользователя с идентификатором " + id);
        return userService.deleteUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен запрос PUT к эндпоинту /users/" + id + "/friends/" + friendId + "." +
                " Пользователи с идентификаторами " + id + " и " + friendId + " теперь друзья");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен запрос DELETE к эндпоинту /users/" + id + "/friends/" + friendId +
                ". Пользователи с идентификаторами " + id + " и " + friendId
                + "больше не друзья");
        userService.removeFriend(id, friendId);
    }

    @GetMapping("{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("Получен запрос GET к эндпоинту /users/" + id + "/friends на получение списка друзей" +
                " пользователя с идентификатором " + id);
        return userService.getFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getSharedFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получен запрос GET к эндпоинту /users/" + id + "/friends/common/" + otherId +
                " на получение списка общих друзей" + " у пользователей с идентификаторами " + id + " и " + otherId);
        return userService.getSharedFriends(id, otherId);
    }

}
