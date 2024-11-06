package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.Collections;
import java.util.List;

@Repository
public class LikeDbStorage implements LikeStorage {
	private static final String SQL_QUERY_ADD = "insert into user_liked_film(user_id, film_id) values (?, ?)";
	private static final String SQL_QUERY_GET = """
			select film_id \n
			from user_liked_film \n
			group by film_id \n
			order by count(user_id) desc \n
			limit ?""";
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
		return jdbcTemplate.update(SQL_QUERY_ADD,
				userId,
				filmId) > 0;
	}

	@Override
	public List<Integer> getPopularFilms(int count) {
		List<Integer> popularFilms = jdbcTemplate.query(SQL_QUERY_GET, (rs, rowNum) -> rs.getInt("film_id"), count);
		if (popularFilms.isEmpty()) {
			return Collections.emptyList();
		}
		return popularFilms;
	}
}