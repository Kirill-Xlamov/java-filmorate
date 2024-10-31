package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

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

	public User(int id, String email, String login, String name, LocalDate birthday) {
		this.id = id;
		this.email = email;
		this.login = login;
		this.name = name;
		this.birthday = birthday;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return id == user.id && Objects.equals(email, user.email) && Objects.equals(login, user.login)
				&& Objects.equals(name, user.name) && Objects.equals(birthday, user.birthday);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, email, login, name, birthday);
	}
}