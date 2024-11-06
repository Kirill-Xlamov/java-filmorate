package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
	private int id;
	@Email
	@NotBlank(message = "Электронная почта не может быть пустой")
	private String email;
	@NotBlank(message = "Логин не может быть пустым и содержать пробелы")
	private String login;
	private String name;
	@PastOrPresent(message = "Дата рождения не может быть в будущем")
	private LocalDate birthday;
}