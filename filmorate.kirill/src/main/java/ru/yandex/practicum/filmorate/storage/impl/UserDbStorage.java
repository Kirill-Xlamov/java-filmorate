package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository("userDbStorage")
public class UserDbStorage implements UserStorage {
	private final JdbcTemplate jdbcTemplate;

	public UserDbStorage(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<User> findAll() {
		String sql = "select * from users";
		List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
		if (users.isEmpty()) {
			return Collections.emptyList();
		}
		return users;
	}

	@Override
	public User create(User user) {
		String sqlQuery = "insert into public.users (email, login, name, birthday) " +
				"values (?, ?, ?, ?)";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(connection -> {
			PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
			stmt.setString(1, user.getEmail());
			stmt.setString(2, user.getLogin());
			stmt.setString(3, user.getName());
			stmt.setDate(4, Date.valueOf(user.getBirthday()));
			return stmt;
		}, keyHolder);

		int id = Objects.requireNonNull(keyHolder.getKey()).intValue();
		user.setId(id);
		return user;
	}

	@Override
	public User update(User user) {
		String sqlQuery = "update public.users set " +
				"email = ?, login = ?, name = ?, birthday= ?" +
				"where user_id = ?";
		jdbcTemplate.update(sqlQuery,
				user.getEmail(),
				user.getLogin(),
				user.getName(),
				user.getBirthday(),
				user.getId());
		return user;
	}

	@Override
	public User get(int id) {
		SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);
		if (!userRows.next()) {
			return null;
		}
		return new User(
				userRows.getInt("user_id"),
				userRows.getString("email"),
				userRows.getString("login"),
				userRows.getString("name"),
				Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate());
	}

	private User makeUser(ResultSet rs) throws SQLException {
		return new User(rs.getInt("user_id"), rs.getString("email"),
				rs.getString("login"), rs.getString("name"),
				rs.getDate("birthday").toLocalDate());
	}
}