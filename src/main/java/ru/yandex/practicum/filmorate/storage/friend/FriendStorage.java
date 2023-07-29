package ru.yandex.practicum.filmorate.storage.friend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Component
public class FriendStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    @Autowired
    public FriendStorage(JdbcTemplate jdbcTemplate, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        List<User> userFriends = getFriends(userId);
        List<User> friendFriends = getFriends(friendId);
        if (userFriends != null) {
            for (User user1 : userFriends) {
                user.addFriend(user1.getId());
            }
        }
        if (friendFriends != null) {
            for (User user2 : friendFriends) {
                friend.addFriend(user2.getId());
            }
        }
        if ((user != null) && (friend != null)) {
            user.addFriend(friendId);
            boolean status = false;
            String sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, userId, friendId, status);
        }
    }

    public void makeFriendshipTrue(Long userId, Long friendId) {
        String sql = "DELETE FROM friends where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sql, friendId, userId);
        jdbcTemplate.update(sql, userId, friendId);
        sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, friendId, userId, true);
        jdbcTemplate.update(sql, userId, friendId, true);
    }

    public void makeFriendshipFalse(Long userId, Long friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, friendId, userId, false);
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if ((user != null) && (friend != null)) {
            user.removeFriend(friendId);
            String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sql, userId, friendId);
            if (friend.getFriends().contains(userId)) {
                // дружба стала невзаимной - нужно поменять статус
                sql = "UPDATE friends SET user_id = ? AND friend_id = ? AND status = ? " +
                        "WHERE user_id = ? AND friend_id = ?";
                jdbcTemplate.update(sql, friendId, userId, false, friendId, userId);
            }
        }
    }

    public List<User> getFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user != null) {
            String sql = "SELECT friend_id, email, login, name, birthday FROM friends" +
                    " INNER JOIN users ON friends.friend_id = users.id WHERE friends.user_id = ?";
            return jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                            rs.getLong("friend_id"),
                            rs.getString("login"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getDate("birthday").toLocalDate(),
                            null),
                    userId
            );
        } else {
            return null;
        }
    }
}
