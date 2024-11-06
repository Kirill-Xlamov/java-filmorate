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
	private static final String SQL_QUERY_ADD = "insert into friends(from_id, to_id, confirmation) values (?, ?, ?)";
	private static final String SQL_QUERY_DELETE = "delete from friends where from_id = ? and to_id = ?";
	private static final String SQL_QUERY_GET = "select * from friends where from_id = ?";
	private final JdbcTemplate jdbcTemplate;

	public FriendsDbStorage(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public int addFriend(int userId, int friendId) {
		boolean isFriend = getFriends(friendId).contains(userId);
		jdbcTemplate.update(SQL_QUERY_ADD,
				userId,
				friendId,
				isFriend);
		return userId;
	}

	@Override
	public boolean removeFriend(int userId, int friendId) {
		return jdbcTemplate.update(SQL_QUERY_DELETE,
				userId,
				friendId) > 0;
	}

	@Override
	public Set<Integer> getFriends(int userId) {
		List<Integer> userFriends = jdbcTemplate.query(SQL_QUERY_GET, (rs, rowNum) -> rs.getInt("to_id"), userId);
		if (userFriends.isEmpty()) {
			return Collections.emptySet();
		}
		return new HashSet<>(userFriends);
	}
}