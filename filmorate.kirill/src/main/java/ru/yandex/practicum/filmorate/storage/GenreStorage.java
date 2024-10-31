package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
	List<Genre> getGenres();

	Genre getGenreById(int genreId);

	int addFilmGenre(int filmId, int genreId);

	List<Genre> getFilmGenreById(int filmId);

	boolean deleteFilmGenre(int filmId, int genreId);
}