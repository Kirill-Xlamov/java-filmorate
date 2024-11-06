package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {
	private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
	private final FilmStorage filmStorage;
	private final UserService userService;
	private final GenreService genreService;
	private final LikeStorage likeStorage;
	private final MpaService mpaService;

	@Autowired
	public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserService userService,
						   LikeStorage likeStorage, MpaService mpaService, GenreService genreService) {
		this.filmStorage = filmStorage;
		this.userService = userService;
		this.genreService = genreService;
		this.likeStorage = likeStorage;
		this.mpaService = mpaService;
	}

	@Override
	public Film addLike(int filmId, int userId) {
		Film film = get(filmId);
		userService.get(userId);
		if (!likeStorage.addLike(userId, filmId)) {
			log.info("Лайк от пользователя с id:{} не добавлен", userId);
		}
		log.info("Лайк от пользователя с id:{} добавлен", userId);
		return film;
	}

	@Override
	public Film removeLike(int filmId, int userId) {
		Film film = get(filmId);
		userService.get(userId);
		if (!likeStorage.removeLike(userId, filmId)) {
			log.info("Лайк от пользователя с id:{} не удален", userId);
		}
		log.info("Лайк от пользователя с id:{} удален", userId);
		return film;
	}

	@Override
	public List<Film> getPopularFilms(int count) {
		List<Integer> films = likeStorage.getPopularFilms(count);
		List<Film> popularFilms = films.stream()
				.map(this::get)
				.collect(Collectors.toList());
		log.info("Получен список популярных фильмов в количестве: {} шт.", popularFilms.size());
		return popularFilms;
	}

	@Override
	public Film get(int id) {
		Film film = filmStorage.get(id);
		if (filmStorage.get(id) == null) {
			throw new ObjectNotFoundException(String.format("Фильм с %s не найден", id));
		}
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
		mpaService.checkMpa(film);
		Film filmAdded = filmStorage.add(film);
		filmAdded = mpaService.setMpaInFilm(filmAdded);
		filmAdded = genreService.recordFilmGenre(filmAdded);
		return filmAdded;
	}

	@Override
	public Film update(Film newFilm) {
		int filmId = newFilm.getId();
		if (filmId == 0) {
			throw new ObjectNotFoundException("Введите фильм, который надо обновить");
		}
		mpaService.checkMpa(newFilm);
		Film filmUpdated = filmStorage.update(newFilm);
		filmUpdated = mpaService.setMpaInFilm(filmUpdated);
		Film oldFilm = get(filmId);
		genreService.updateGenreInFilm(newFilm, oldFilm);
		log.info("Фильм обновлен: {}", newFilm);
		return filmUpdated;
	}

	@Override
	public List<Film> findAll() {
		List<Film> filmStorageAll = filmStorage.findAll();
		filmStorageAll.forEach(genreService::setGenreById);
		log.info("Количество найденных фильмов {}", filmStorageAll.size());
		return filmStorageAll;
	}
}