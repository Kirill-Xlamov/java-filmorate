package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface LikeStorage {
	boolean addLike(int userId, int filmId);

	boolean removeLike(int userId, int filmId);

	List<Integer> getPopularFilms(int count);
}