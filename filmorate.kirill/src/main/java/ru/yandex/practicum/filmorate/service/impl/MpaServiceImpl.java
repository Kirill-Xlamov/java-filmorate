package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@Slf4j
public class MpaServiceImpl implements MpaService {
	private final MpaStorage mpaStorage;

	public MpaServiceImpl(MpaStorage mpaStorage) {
		this.mpaStorage = mpaStorage;
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
		log.info("Получен рейтинг: {}", mpaById.id());
		return mpaById;
	}

	@Override
	public Film setMpaInFilm(Film film) {
		Mpa mpaWhitName = checkMpa(film);
		film.setMpa(mpaWhitName);
		return film;
	}

	@Override
	public Mpa checkMpa(Film film) {
		Mpa mpaWithoutName = film.getMpa();
		int mpaId = mpaWithoutName.id();
		Mpa mpaWhitName = mpaStorage.getMpaById(mpaId);
		if (mpaWhitName == null) {
			throw new ValidationException(String.format("Рейтинг c id: %s не найден", mpaId));
		}
		log.info("Проверен рейтинг: {}", mpaWhitName.id());
		return mpaWhitName;
	}
}