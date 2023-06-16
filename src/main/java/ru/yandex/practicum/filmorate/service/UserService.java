package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Component
public class UserService {

    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            throw new ValidationException("Один из идентификаторов передан пустым");
        }
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить в друзья самого себя");
        }
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null) {
            throw new UserNotFoundException("Несуществующий пользователь не может добавлять в друзья");
        }
        if (friend == null) {
            throw new UserNotFoundException("Попытка добавить в друзья несуществующего пользователя");
        }
        user.addFriend(friendId);
        friend.addFriend(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            throw new ValidationException("Один из идентификаторов передан пустым");
        }
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить в друзья самого себя");
        }
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null) {
            throw new UserNotFoundException("Несуществующий пользователь не может добавлять в друзья");
        }
        if (friend == null) {
            throw new UserNotFoundException("Попытка добавить в друзья несуществующего пользователя");
        }
        user.removeFriend(friendId);
        friend.removeFriend(userId);
    }

    public List<User> getFriends(Long userId) {
        if (userId == null) {
            throw new ValidationException("Один из идентификаторов передан пустым");
        }
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("Несуществующий пользователь не может добавлять в друзья");
        }
        List<User> friends = new ArrayList<>();
        for (long i : user.getFriends()) {
            friends.add(userStorage.getUserById(i));
        }
        return friends;
    }

    public List<User> getSharedFriends(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            throw new ValidationException("Один из идентификаторов передан пустым");
        }
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить в друзья самого себя");
        }
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null || friend == null) {
            throw new UserNotFoundException("Невозможно посмотреть список друзей несуществующего пользователя");
        }
        Set<Long> sameFriends =new HashSet<>(user.getFriends());
        sameFriends.retainAll(friend.getFriends());
        List<Long> finalFriends = new ArrayList<>(sameFriends);
        List<User> friends = new ArrayList<>();
        for (long i : finalFriends) {
            friends.add(userStorage.getUserById(i));
        }
        return friends;
    }

}
