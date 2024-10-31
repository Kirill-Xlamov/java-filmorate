package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Repository
public class GenreDbStorage implements GenreStorage {
	private final JdbcTemplate jdbcTemplate;

	public GenreDbStorage(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<Genre> getGenres() {
		String sql = "select * from genres";
		List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
		if (genres.isEmpty()) {
			return Collections.emptyList();
		}
		return genres;
	}

	@Override
	public Genre getGenreById(int genreId) {
		SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from genres where genre_id = ?", genreId);
		if (!genreRows.next()) {
			return null;
		}
		return new Genre(
				genreRows.getInt("genre_id"),
				genreRows.getString("name"));
	}

	@Override
	public List<Genre> getFilmGenreById(int filmId) {
		String sql = "select g.genre_id, g.name " +
				"from genre_film as gf " +
				"inner join genres as g on gf.genre_id = g.genre_id " +
				"where film_id = ?";
		List<Genre> filmGenres = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), filmId);
		if (filmGenres.isEmpty()) {
			return Collections.emptyList();
		}
		return filmGenres;
	}

	@Override
	public int addFilmGenre(int filmId, int genreId) {
		String sqlQuery = "insert into public.genre_film (film_id, genre_id) " +
				"values (?, ?)";
		jdbcTemplate.update(sqlQuery,
				filmId,
				genreId);
		return filmId;
	}

	@Override
	public boolean deleteFilmGenre(int filmId, int genreId) {
		String sqlQuery = "delete from genre_film where film_id = ? and genre_id = ?";
		return jdbcTemplate.update(sqlQuery,
				filmId,
				genreId) > 0;
	}

	private Genre makeGenre(ResultSet rs) throws SQLException {
		return new Genre(rs.getInt("genre_id"), rs.getString("name"));
	}
}