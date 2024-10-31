package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {
	private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
	private final FilmStorage filmStorage;
	private final UserService userService;
	private final GenreStorage genreStorage;
	private final LikeStorage likeStorage;
	private final MpaStorage mpaStorage;

	@Autowired
	public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserService userService,
						   GenreStorage genreStorage, LikeStorage likeStorage, MpaStorage mpaStorage) {
		this.filmStorage = filmStorage;
		this.userService = userService;
		this.genreStorage = genreStorage;
		this.likeStorage = likeStorage;
		this.mpaStorage = mpaStorage;
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
		Mpa mpaWithoutName = film.getMpa();
		int mpaId = mpaWithoutName.getId();
		List<Genre> genresWithoutName = film.getGenres();

		Mpa mpaByIdWithName = checkMpaById(mpaId);
		Film filmAdded = filmStorage.add(film);
		filmAdded.setMpa(mpaByIdWithName);
		if (genresWithoutName == null) {
			log.info("Добавлен новый фильм без жанров {}", filmAdded.getName());
			return filmAdded;
		}
		int filmId = filmAdded.getId();
		List<Genre> genreWithName = genresWithoutName.stream()
				.map(Genre::getId)
				.distinct()
				.map(this::checkGenreById)
				.peek(genre -> genreStorage.addFilmGenre(filmId, genre.getId()))
				.toList();
		filmAdded.setGenres(genreWithName);
		log.info("Добавлен новый фильм {}", filmAdded.getName());
		return filmAdded;
	}

	@Override
	public Film update(Film newFilm) {
		int filmId = newFilm.getId();
		if (filmId == 0) {
			throw new ObjectNotFoundException("Введите фильм, который надо обновить");
		}
		Mpa mpaWithoutName = newFilm.getMpa();
		Mpa mpaWithName = checkMpaById(mpaWithoutName.getId());
		newFilm.setMpa(mpaWithName);
		Film oldFilm = get(filmId);
		Film filmUpdated = filmStorage.update(newFilm);
		updateGenreInFilm(newFilm, oldFilm);
		log.info("Фильм обновлен: {}", newFilm);
		return filmUpdated;
	}

	private void updateGenreInFilm(Film newFilm, Film filmOld) {
		int filmId = newFilm.getId();
		List<Genre> genresOld = filmOld.getGenres();
		List<Genre> genresNew = newFilm.getGenres();

		if (genresOld == null && genresNew != null) {
			genresNew.stream()
					.map(Genre::getId)
					.forEach(genreId -> genreStorage.addFilmGenre(filmId, genreId));
			return;
		}
		if (genresNew == null) {
			return;
		}
		List<Integer> genresOldInt = genresOld.stream()
				.map(Genre::getId)
				.toList();
		List<Integer> genresNewInt = genresNew.stream()
				.map(Genre::getId)
				.toList();
		List<Integer> forDel = genresOld.stream()
				.map(Genre::getId)
				.collect(Collectors.toList());
		List<Integer> duplicated = genresOld.stream()
				.map(Genre::getId)
				.collect(Collectors.toList());
		List<Integer> forUpd = genresNew.stream()
				.map(Genre::getId)
				.collect(Collectors.toList());

		duplicated.retainAll(genresOldInt);
		forDel.removeAll(genresNewInt);
		forUpd.removeAll(duplicated);
		forDel.forEach(genreId -> genreStorage.deleteFilmGenre(filmId, genreId));
		forUpd.forEach(genreId -> genreStorage.addFilmGenre(filmId, genreId));
	}

	@Override
	public List<Film> findAll() {
		List<Film> filmStorageAll = filmStorage.findAll();
		filmStorageAll.forEach(this::setGenreById);
		log.info("Количество найденных фильмов {}", filmStorageAll.size());
		return filmStorageAll;
	}

	private void setGenreById(Film film) {
		int id = film.getId();
		List<Genre> filmGenreById = genreStorage.getFilmGenreById(id);
		film.setGenres(filmGenreById);
	}

	@Override
	public List<Genre> getGenres() {
		List<Genre> genres = genreStorage.getGenres();
		log.info("Всего жанров: {}", genres.size());
		return genres;
	}

	@Override
	public Genre getGenreById(int genreId) {
		Genre genreById = genreStorage.getGenreById(genreId);
		if (genreById == null) {
			throw new ObjectNotFoundException(String.format("Жанр c id: %s не найден", genreId));
		}
		log.info("Получен жанр: {}", genreById.getName());
		return genreById;
	}

	@Override
	public List<Mpa> getMpa() {
		List<Mpa> mpa = mpaStorage.getMpa();
		log.info("Всего рейтингов: {}", mpa.size());
		return mpa;
	}

	@Override
	public Mpa getMpaById(int mpaId) {
		Mpa mpaById = mpaStorage.getMpaById(mpaId);
		if (mpaById == null) {
			throw new ObjectNotFoundException(String.format("Рейтинг c id: %s не найден", mpaId));
		}
		log.info("Получен рейтинг: {}", mpaById.getName());
		return mpaById;
	}

	private Genre checkGenreById(int genreId) {
		Genre genreById = genreStorage.getGenreById(genreId);
		if (genreById == null) {
			throw new ValidationException(String.format("Жанр c id: %s не найден", genreId));
		}
		log.info("Проверен жанр: {}", genreById.getName());
		return genreById;
	}

	private Mpa checkMpaById(int mpaId) {
		Mpa mpaById = mpaStorage.getMpaById(mpaId);
		if (mpaById == null) {
			throw new ValidationException(String.format("Рейтинг c id: %s не найден", mpaId));
		}
		log.info("Проверен рейтинг: {}", mpaById.getName());
		return mpaById;
	}
}