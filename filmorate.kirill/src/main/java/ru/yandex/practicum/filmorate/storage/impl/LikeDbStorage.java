package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.Collections;
import java.util.List;

@Repository
public class LikeDbStorage implements LikeStorage {
	private final JdbcTemplate jdbcTemplate;

	public LikeDbStorage(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public boolean addLike(int userId, int filmId) {
		String sqlQuery = "insert into user_liked_film(user_id, film_id) " +
				"values (?, ?)";
		return jdbcTemplate.update(sqlQuery,
				userId,
				filmId) > 0;
	}

	@Override
	public boolean removeLike(int userId, int filmId) {
		String sqlQuery = "delete from user_liked_film where user_id = ? and film_id = ?";
		return jdbcTemplate.update(sqlQuery,
				userId,
				filmId) > 0;
	}

	@Override
	public List<Integer> getPopularFilms(int count) {
		String sql = "select film_id " +
				"from user_liked_film " +
				"group by film_id " +
				"order by count(user_id) desc " +
				"limit ?";
		List<Integer> popularFilms = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("film_id"), count);
		if (popularFilms.isEmpty()) {
			return Collections.emptyList();
		}
		return popularFilms;
	}
}