package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class GenreDbStorage implements GenreStorage {
	private static final String SQL_QUERY_GET_GENRES = "select * from genres";
	private static final String SQL_QUERY_GET_GENRE = "select * from genres where genre_id = ?";
	private static final String SQL_QUERY_GET_FILM_GENRE = """
			select g.genre_id, g.name \n
			from genre_film as gf \n
			inner join genres as g on gf.genre_id = g.genre_id \n
			where film_id = ?""";
	private static final String SQL_QUERY_ADD_FILM_GENRE = """
			insert into public.genre_film (film_id, genre_id) values (?, ?)""";
	private static final String SQL_QUERY_DELETE_FILM_GENRE = """
			delete from genre_film where film_id = ? and genre_id = ?""";
	private final JdbcTemplate jdbcTemplate;

	public GenreDbStorage(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<Genre> getGenres() {
		List<Genre> genres = jdbcTemplate.query(SQL_QUERY_GET_GENRES, (rs, rowNum) -> makeGenre(rs));
		if (genres.isEmpty()) {
			return Collections.emptyList();
		}
		return genres;
	}

	@Override
	public Genre getGenreById(int genreId) {
		SqlRowSet genreRows = jdbcTemplate.queryForRowSet(SQL_QUERY_GET_GENRE, genreId);
		if (!genreRows.next()) {
			return null;
		}
		return new Genre(
				genreRows.getInt("genre_id"),
				genreRows.getString("name"));
	}

	@Override
	public List<Genre> getFilmGenreById(int filmId) {
		List<Genre> filmGenres = jdbcTemplate.query(SQL_QUERY_GET_FILM_GENRE, (rs, rowNum) -> makeGenre(rs), filmId);
		if (filmGenres.isEmpty()) {
			return Collections.emptyList();
		}
		return filmGenres;
	}

	@Override
	public int addFilmGenre(int filmId, int genreId) {
		jdbcTemplate.update(SQL_QUERY_ADD_FILM_GENRE,
				filmId,
				genreId);
		return filmId;
	}

	@Override
	public boolean deleteFilmGenre(int filmId, int genreId) {
		return jdbcTemplate.update(SQL_QUERY_DELETE_FILM_GENRE,
				filmId,
				genreId) > 0;
	}

	private Genre makeGenre(ResultSet rs) throws SQLException {
		return new Genre(rs.getInt("genre_id"), rs.getString("name"));
	}
}