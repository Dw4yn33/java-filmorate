package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public List<User> getUsers() {
        List<User> users = userStorage.getUsers();
        List<User> newUsers = new ArrayList<>();
        if (users != null) {
            for (User user : users) {
                newUsers.add(getUserById(user.getId()));
            }
        }
        return newUsers;
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User getUserById(Long id) {
        User user = userStorage.getUserById(id);
        List<User> friends = friendStorage.getFriends(id);
        if (friends != null) {
            for (User friend : friends) {
                user.addFriend(friend.getId());
            }
        }
        return user;
    }

    public User deleteUserById(Long id) {
        return userStorage.deleteUserById(id);
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId == friendId) {
            throw new ValidationException("Нельзя добавить самого себя в друзья!");
        }
        if (getUserById(userId).getFriends().contains(friendId)) {
            throw new ValidationException("Ошибка: нельзя отправлять несколько запросов в друзья");
        }
        if (getUserById(friendId).getFriends().contains(userId)) {
            friendStorage.addFriend(userId, friendId);
            friendStorage.makeFriendshipTrue(userId, friendId);
        } else friendStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        if (userId == friendId) {
            throw new ValidationException("Нельзя удалить самого себя из друзей!");
        }
        if (getUserById(friendId).getFriends().contains(userId)
                && getUserById(userId).getFriends().contains(friendId)) {
            friendStorage.deleteFriend(userId, friendId);
            friendStorage.deleteFriend(friendId, userId);
            friendStorage.makeFriendshipFalse(userId,friendId);
        }
        friendStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        List<User> friends = new ArrayList<>();
        if (userId != null) {
            friends = friendStorage.getFriends(userId);
        }
        return friends;
    }

    public List<User> getSharedFriends(Long firstUserId, Long secondUserId) {

        User firstUser = userStorage.getUserById(firstUserId);
        User secondUser = userStorage.getUserById(secondUserId);
        Set<User> intersection = null;

        if ((firstUser != null) && (secondUser != null)) {
            intersection = new HashSet<>(friendStorage.getFriends(firstUserId));
            intersection.retainAll(friendStorage.getFriends(secondUserId));
        }
        return new ArrayList<User>(intersection);
    }

}
