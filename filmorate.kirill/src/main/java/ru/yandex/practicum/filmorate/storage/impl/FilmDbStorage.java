package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
	private static final String SQL_QUERY_ADD = """ 
			insert into public.films (name, description, releaseDate, duration, mpa_id) values (?, ?, ?, ?, ?)""";
	private static final String SQL_QUERY_UPDATE = """
			update public.films set name = ?, description = ?, releaseDate = ?, duration = ?, mpa_id = ? where film_id = ?""";
	private static final String SQL_QUERY_FIND_ALL = "select * from films";
	private static final String SQL_QUERY_GET = """
			select * \n
			from films \n
			where film_id = ?""";
	private final JdbcTemplate jdbcTemplate;
	private final MpaStorage mpaStorage;
	private final GenreStorage genreStorage;

	public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaStorage mpaStorage, GenreStorage genreStorage) {
		this.jdbcTemplate = jdbcTemplate;
		this.mpaStorage = mpaStorage;
		this.genreStorage = genreStorage;
	}

	@Override
	public Film add(Film film) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(connection -> {
			PreparedStatement stmt = connection.prepareStatement(SQL_QUERY_ADD, new String[]{"film_id"});
			stmt.setString(1, film.getName());
			stmt.setString(2, film.getDescription());
			stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
			stmt.setInt(4, film.getDuration());
			stmt.setInt(5, film.getMpa().id());
			return stmt;
		}, keyHolder);

		int id = Objects.requireNonNull(keyHolder.getKey()).intValue();
		film.setId(id);
		return film;
	}

	@Override
	public Film update(Film film) {
		jdbcTemplate.update(SQL_QUERY_UPDATE,
				film.getName(),
				film.getDescription(),
				film.getReleaseDate(),
				film.getDuration(),
				film.getMpa().id(),
				film.getId());
		return film;
	}

	@Override
	public List<Film> findAll() {
		List<Film> users = jdbcTemplate.query(SQL_QUERY_FIND_ALL, (rs, rowNum) -> makeFilm(rs));
		if (users.isEmpty()) {
			return Collections.emptyList();
		}
		return users;
	}

	@Override
	public Film get(int id) {
		SqlRowSet filmRows = jdbcTemplate.queryForRowSet(SQL_QUERY_GET, id);
		if (!filmRows.next()) {
			return null;
		}
		return new Film(
				filmRows.getInt("film_id"),
				filmRows.getString("name"),
				filmRows.getString("description"),
				Objects.requireNonNull(filmRows.getDate("releaseDate")).toLocalDate(),
				filmRows.getInt("duration"),
				mpaStorage.getMpaById(filmRows.getInt("mpa_id")),
				genreStorage.getFilmGenreById(id));
	}

	private Film makeFilm(ResultSet rs) throws SQLException {
		return new Film(rs.getInt("film_id"), rs.getString("name"),
				rs.getString("description"), rs.getDate("releaseDate").toLocalDate(),
				rs.getInt("duration"), mpaStorage.getMpaById(rs.getInt("mpa_id")),
				genreStorage.getFilmGenreById(rs.getInt("film_id")));
	}
}