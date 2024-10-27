package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
	private final FilmService filmService;

	@Autowired
	public FilmController(FilmService filmService) {
		this.filmService = filmService;
	}

	@GetMapping
	public List<Film> findAll() {
		return filmService.findAll();
	}

	@PostMapping
	public Film add(@Valid @RequestBody Film film) {
		return filmService.add(film);
	}

	@PutMapping
	public Film update(@Valid @RequestBody Film film) {
		return filmService.update(film);
	}

	@PutMapping("/{id}/like/{userId}")
	public Film addLike(@PathVariable int id, @PathVariable int userId) {
		return filmService.addLike(id, userId);
	}

	@DeleteMapping("/{id}/like/{userId}")
	public Film removeLike(@PathVariable int id, @PathVariable int userId) {
		return filmService.removeLike(id, userId);
	}

	@GetMapping("/popular")
	public List<Film> popularFilms(@RequestParam(defaultValue = "10") int count) {
		return filmService.getPopularFilms(count);
	}
}