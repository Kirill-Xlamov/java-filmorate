package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping
@Slf4j
public class FilmController {
	private final FilmService filmService;

	@Autowired
	public FilmController(FilmService filmService) {
		this.filmService = filmService;
	}

	@GetMapping("/films")
	public List<Film> findAll() {
		return filmService.findAll();
	}

	@PostMapping("/films")
	public Film add(@Valid @RequestBody Film film) {
		return filmService.add(film);
	}

	@PutMapping("/films")
	public Film update(@Valid @RequestBody Film film) {
		return filmService.update(film);
	}

	@PutMapping("/films/{id}/like/{userId}")
	public Film addLike(@PathVariable int id, @PathVariable int userId) {
		return filmService.addLike(id, userId);
	}

	@DeleteMapping("/films/{id}/like/{userId}")
	public Film removeLike(@PathVariable int id, @PathVariable int userId) {
		return filmService.removeLike(id, userId);
	}

	@GetMapping("/films/popular")
	public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
		return filmService.getPopularFilms(count);
	}

	@GetMapping("/genres")
	public List<Genre> getGenre() {
		return filmService.getGenres();
	}

	@GetMapping("/genres/{genreId}")
	public Genre getGenreById(@PathVariable int genreId) {
		return filmService.getGenreById(genreId);
	}

	@GetMapping("/mpa")
	public List<Mpa> getMpa() {
		return filmService.getMpa();
	}

	@GetMapping("/mpa/{mpaId}")
	public Mpa getMpaById(@PathVariable int mpaId) {
		return filmService.getMpaById(mpaId);
	}

	@GetMapping("/films/{id}")
	public Film update(@PathVariable int id) {
		return filmService.get(id);
	}
}