package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
	private int id;
	@NotBlank(message = "Название не может быть пустым")
	private String name;
	@Size(max = 200, message = "Максимальная длина описания — 200 символов")
	private String description;
	@PastOrPresent
	private LocalDate releaseDate;
	@Positive(message = "Продолжительность фильма должна быть положительной")
	private int duration;
	private final Set<Integer> likesByUser;

	public Film(int id, String name, String description, LocalDate releaseDate, int duration) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.releaseDate = releaseDate;
		this.duration = duration;
		likesByUser = new HashSet<>();
	}
}