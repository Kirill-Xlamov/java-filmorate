package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
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
	private Mpa mpa;
	private List<Genre> genres;

	public Film(int id, String name, String description, LocalDate releaseDate,
				int duration, Mpa mpa, List<Genre> genres) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.releaseDate = releaseDate;
		this.duration = duration;
		this.mpa = mpa;
		this.genres = genres;
	}
}