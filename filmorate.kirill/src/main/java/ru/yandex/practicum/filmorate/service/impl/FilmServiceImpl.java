package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {
	private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
	private final FilmStorage filmStorage;
	private final UserService userService;

	@Autowired
	public FilmServiceImpl(FilmStorage filmStorage, UserService userService) {
		this.filmStorage = filmStorage;
		this.userService = userService;
	}

	@Override
	public Film addLike(int filmId, int userId) {
		Film film = get(filmId);
		Set<Integer> likesByUser = film.getLikesByUser();
		userService.get(userId);
		likesByUser.add(userId);
		log.info("Лайк от пользователя с id:{} добавлен", userId);
		return film;
	}

	@Override
	public Film removeLike(int filmId, int userId) {
		Film film = get(filmId);
		Set<Integer> likesByUser = film.getLikesByUser();
		userService.get(userId);
		likesByUser.remove(userId);
		log.info("Лайк от пользователя с id:{} удален", userId);
		return film;
	}

	@Override
	public List<Film> getPopularFilms(int count) {
		List<Film> films = filmStorage.findAll().stream()
				.sorted((f1, f2) -> f2.getLikesByUser().size() - f1.getLikesByUser().size())
				.limit(count)
				.collect(Collectors.toList());
		log.info("Получен список популярных фильмов в количестве: {} шт.", count);
		return films;
	}

	@Override
	public Film get(int id) {
		if (filmStorage.get(id) == null) {
			throw new ObjectNotFoundException(String.format("Фильм с %s не найден", id));
		}
		Film film = filmStorage.get(id);
		log.info("Получен фильм с id: {}", id);
		return film;
	}

	@Override
	public Film add(Film film) {
		if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
			log.info("Не пройдена валидация releaseDate");
			throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
		}
		if (film.getId() != 0) {
			throw new ValidationException("При добавлении id должен быть 0");
		}
		Film filmAdded = filmStorage.add(film);
		log.info("Добавлен новый фильм {}", filmAdded);
		return filmAdded;
	}

	@Override
	public Film update(Film film) {
		int filmId = film.getId();
		if (filmStorage.get(filmId) == null || filmId == 0) {
			throw new ObjectNotFoundException("Введите фильм, который надо обновить");
		}
		Film filmUpdated = filmStorage.update(film);
		log.info("Фильм обновлен: {}", film);
		return filmUpdated;
	}

	@Override
	public List<Film> findAll() {
		log.info("Количество найденных фильмов {}", filmStorage.findAll().size());
		return filmStorage.findAll();
	}
}