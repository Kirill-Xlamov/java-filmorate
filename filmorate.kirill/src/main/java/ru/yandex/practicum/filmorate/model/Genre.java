package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genre {
	private final int id;
	private String name;

	public Genre(int id, String name) {
		this.id = id;
		this.name = name;
	}
}