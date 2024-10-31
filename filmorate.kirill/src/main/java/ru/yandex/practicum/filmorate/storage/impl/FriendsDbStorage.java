package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository("friendDbStorage")
public class FriendsDbStorage implements FriendStorage {
	private final JdbcTemplate jdbcTemplate;

	public FriendsDbStorage(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public int addFriend(int userId, int friendId) {
		String sqlQuery = "insert into friends(from_id, to_id, confirmation) " +
				"values (?, ?, ?)";
		boolean isFriend = getFriends(friendId).contains(userId);
		jdbcTemplate.update(sqlQuery,
				userId,
				friendId,
				isFriend);
		return userId;
	}

	@Override
	public boolean removeFriend(int userId, int friendId) {
		String sqlQuery = "delete from friends where from_id = ? and to_id = ?";
		return jdbcTemplate.update(sqlQuery,
				userId,
				friendId) > 0;
	}

	@Override
	public Set<Integer> getFriends(int userId) {
		String sql = "select * from friends where from_id = ?";
		List<Integer> userFriends = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("to_id"), userId);
		if (userFriends.isEmpty()) {
			return Collections.emptySet();
		}
		return new HashSet<>(userFriends);
	}
}