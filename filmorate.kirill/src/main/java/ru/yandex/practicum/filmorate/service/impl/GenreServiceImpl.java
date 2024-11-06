package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GenreServiceImpl implements GenreService {
	private final GenreStorage genreStorage;

	public GenreServiceImpl(GenreStorage genreStorage) {
		this.genreStorage = genreStorage;
	}

	@Override
	public Film recordFilmGenre(Film film) {
		List<Genre> genresWithoutName = film.getGenres();
		if (genresWithoutName == null) {
			log.info("Добавлен новый фильм без жанров {}", film.getName());
			return film;
		}
		int filmId = film.getId();
		List<Genre> genreWithName = genresWithoutName.stream()
				.map(Genre::id)
				.distinct()
				.map(this::checkGenreById)
				.peek(genre -> genreStorage.addFilmGenre(filmId, genre.id()))
				.toList();
		film.setGenres(genreWithName);
		return film;
	}

	@Override
	public void updateGenreInFilm(Film newFilm, Film filmOld) {
		int filmId = newFilm.getId();
		List<Genre> genresOld = filmOld.getGenres();
		List<Genre> genresNew = newFilm.getGenres();

		if (genresOld == null && genresNew != null) {
			genresNew.stream()
					.map(Genre::id)
					.forEach(genreId -> genreStorage.addFilmGenre(filmId, genreId));
			log.info("Жанры фильма \"{}\" установлены", newFilm.getName());
			return;
		}
		if (genresNew == null) {
			log.info("Жанры фильма \"{}\" не изменились", newFilm.getName());
			return;
		}
// Пример работы: есть два списка А(1,2,3,6) и Б(2,3,7,8), нам нужно получить три списка: уникальные номера в списке А,
// уникальные в Б, и список совпадающих номеров.
//	Будем использовать методы remove и retain, т.к. они изменяют список, то создаем три списка согласно нашей задаче.
		List<Integer> filmGenreForDeleted = genresOld.stream()
				.map(Genre::id)
				.collect(Collectors.toList());
		List<Integer> filmGenreForUpdate = genresNew.stream()
				.map(Genre::id)
				.collect(Collectors.toList());
		List<Integer> duplicates = new ArrayList<>(filmGenreForDeleted);
// Для получения дубликатов мы из А и Б сохарняем в список duplicates(2,3)
		duplicates.retainAll(filmGenreForDeleted);
// Для получения значений на удаление из списка А вычтем Б и получим filmGenreForDeleted(1,6)
		filmGenreForDeleted.removeAll(filmGenreForUpdate);
// Для получения значений на обновление из списка Б вычтем duplicates и получим filmGenreForUpdate(7,8)
		filmGenreForUpdate.removeAll(duplicates);
// Нужное обновлем и удаляем
		filmGenreForDeleted.forEach(genreId -> genreStorage.deleteFilmGenre(filmId, genreId));
		filmGenreForUpdate.forEach(genreId -> genreStorage.addFilmGenre(filmId, genreId));
		setGenreById(newFilm);
		log.info("Жанры фильма \"{}\" обновлены", newFilm.getName());
	}

	@Override
	public void setGenreById(Film film) {
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
		log.info("Получен жанр: {}", genreById.name());
		return genreById;
	}

	private Genre checkGenreById(int genreId) {
		Genre genreById = genreStorage.getGenreById(genreId);
		if (genreById == null) {
			throw new ValidationException(String.format("Жанр c id: %s не найден", genreId));
		}
		log.info("Проверен жанр: {}", genreById.name());
		return genreById;
	}
}