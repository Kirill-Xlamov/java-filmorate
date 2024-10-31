package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.impl.FilmServiceImpl;
import ru.yandex.practicum.filmorate.service.impl.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.impl.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmServiceImpl.class, FilmDbStorage.class, UserServiceImpl.class, GenreDbStorage.class,
		UserDbStorage.class, FriendsDbStorage.class, LikeDbStorage.class, MpaDbStorage.class})
class FilmServiceTests {
	private final FilmService filmService;
	private final UserService userService;

	Film film = new Film(0, "filmName", "descript",
			LocalDate.of(2001, 11, 20), 120, new Mpa(1, null),
			Arrays.asList(new Genre(1, null), new Genre(2, null)));

	@DisplayName("Проверка добавления фильма")
	@Test
	public void testAdd() {
		Film addedFilm = filmService.add(film);
		assertThat(addedFilm).isEqualTo(film);
	}

	@DisplayName("Проверка получения фильма из базы")
	@Test
	public void testGet() {
		Film addedFilm = filmService.add(film);
		Film film1 = filmService.get(addedFilm.getId());
		assertThat(film1).isEqualTo(addedFilm);
	}

	@DisplayName("Проверка обновления фильма из базы")
	@Test
	public void testUpdate() {
		Film newFilm = new Film(0, "newFilmName", "newDescript",
				LocalDate.of(2021, 3, 15), 140, new Mpa(1, null),
				Arrays.asList(new Genre(1, null), new Genre(2, null)));

		filmService.add(film);
		newFilm.setId(film.getId());
		Film updateFilm = filmService.update(newFilm);
		Film film1 = filmService.get(film.getId());
		assertThat(newFilm).isEqualTo(updateFilm);
		assertThat(film1).isEqualTo(updateFilm);
	}

	@DisplayName("Проверка получения всех из базы")
	@Test
	public void testFindAll() {
		filmService.add(film);
		Film newFilm = new Film(0, "newFilmName", "newDescript",
				LocalDate.of(2021, 3, 15), 140, new Mpa(1, null),
				Arrays.asList(new Genre(1, null), new Genre(2, null)));
		filmService.add(newFilm);

		Film film1 = filmService.get(film.getId());
		Film film2 = filmService.get(newFilm.getId());

		List<Film> films = filmService.findAll();
		assertThat(films.size()).isEqualTo(2);
		assertThat(films.get(0)).isEqualTo(film1);
		assertThat(films.get(1)).isEqualTo(film2);
	}

	@DisplayName("Проверка добавления/удаления лайка фильму")
	@Test
	public void testAddLike() {
		Film film1 = new Film(0, "FilmName1", "1",
				LocalDate.of(2011, 1, 11), 141, new Mpa(1, null),
				Arrays.asList(new Genre(1, null), new Genre(2, null)));
		Film film2 = new Film(0, "FilmName2", "2",
				LocalDate.of(2012, 2, 12), 142, new Mpa(1, null),
				Arrays.asList(new Genre(1, null), new Genre(2, null)));
		Film film3 = new Film(0, "FilmName3", "3",
				LocalDate.of(2013, 3, 13), 143, new Mpa(1, null),
				Arrays.asList(new Genre(1, null), new Genre(2, null)));
		User user1 = new User(0, "1@mail.ru", "login1", "T1", LocalDate.of(2001, 1, 11));
		User user2 = new User(0, "2@mail.ru", "login2", "T2", LocalDate.of(2002, 2, 22));
		User user3 = new User(0, "3@mail.ru", "login3", "T3", LocalDate.of(2003, 3, 23));
		User user4 = new User(0, "4@mail.ru", "login4", "T4", LocalDate.of(2004, 4, 24));
		User user5 = new User(0, "5@mail.ru", "login5", "T5", LocalDate.of(2005, 5, 25));
		userService.create(user1);
		userService.create(user2);
		userService.create(user3);
		userService.create(user4);
		userService.create(user5);
		filmService.add(film1);
		filmService.add(film2);
		filmService.add(film3);

		filmService.addLike(film1.getId(), user1.getId());
		filmService.addLike(film1.getId(), user2.getId());
		filmService.addLike(film1.getId(), user3.getId());

		filmService.addLike(film2.getId(), user1.getId());
		filmService.addLike(film2.getId(), user3.getId());

		filmService.addLike(film3.getId(), user2.getId());

		List<Film> popularFilms = filmService.getPopularFilms(3);
		assertThat(popularFilms.size()).isEqualTo(3);
		assertThat(popularFilms.get(0)).isEqualTo(film1);
		assertThat(popularFilms.get(2)).isEqualTo(film3);

		filmService.removeLike(film1.getId(), user1.getId());
		filmService.removeLike(film1.getId(), user2.getId());
		popularFilms = filmService.getPopularFilms(3);

		assertThat(popularFilms.size()).isEqualTo(3);
		assertThat(popularFilms.get(0)).isEqualTo(film2);
		assertThat(popularFilms.get(1)).isEqualTo(film1);
	}

	@DisplayName("Проверка получения списка жанров")
	@Test
	public void testGetGenres() {
		List<Genre> genres = filmService.getGenres();
		assertThat(genres.size()).isEqualTo(6);
		assertThat(genres.get(0)).isEqualTo(new Genre(1, "Comedy"));
	}

	@DisplayName("Проверка получения жанра по id")
	@Test
	public void testGetGenreById() {
		Genre genreById = filmService.getGenreById(3);
		assertThat(genreById).isEqualTo(new Genre(3, "Cartoon"));
	}

	@DisplayName("Проверка получения списка рейтингов")
	@Test
	public void testGetMpa() {
		List<Mpa> mpa = filmService.getMpa();
		assertThat(mpa.size()).isEqualTo(5);
		assertThat(mpa.get(0)).isEqualTo(new Mpa(1, "G"));
	}

	@DisplayName("Проверка получения рейтинга по id")
	@Test
	public void testGetMpaById() {
		Mpa mpaById = filmService.getMpaById(3);
		assertThat(mpaById).isEqualTo(new Mpa(3, "PG-13"));
	}
}