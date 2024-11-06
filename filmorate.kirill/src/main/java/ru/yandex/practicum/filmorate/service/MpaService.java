package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaService {
	List<Mpa> getMpa();

	Mpa getMpaById(int mpaId);

	Film setMpaInFilm(Film film);

	Mpa checkMpa(Film film);
}