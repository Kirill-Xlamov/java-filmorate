package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreService {
	Film recordFilmGenre(Film film);

	void updateGenreInFilm(Film newFilm, Film filmOld);

	void setGenreById(Film film);

	List<Genre> getGenres();

	Genre getGenreById(int genreId);
}