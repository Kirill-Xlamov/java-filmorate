package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
	private int id;
	@Email
	@NotBlank(message = "Электронная почта не может быть пустой")
	private final String email;
	@NotBlank(message = "Логин не может быть пустым и содержать пробелы")
	private final String login;
	private String name;
	@PastOrPresent(message = "Дата рождения не может быть в будущем")
	private final LocalDate birthday;
	private final Set<Integer> friends;

	public User(int id, String email, String login, String name, LocalDate birthday) {
		this.id = id;
		this.email = email;
		this.login = login;
		this.name = name;
		this.birthday = birthday;
		friends = new HashSet<>();
	}
}