package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
	protected final Map<Integer, User> users = new HashMap<>();
	private int id = 1;

	@Override
	public List<User> findAll() {
		if (users.isEmpty()) {
			return Collections.emptyList();
		}
		return new ArrayList<>(users.values());
	}

	@Override
	public User create(User user) {
		setUserLocalId(user);
		users.put(user.getId(), user);
		return user;
	}

	@Override
	public User update(User user) {
		int userId = user.getId();
		users.put(userId, user);
		return user;
	}

	@Override
	public User get(int id) {
		return users.get(id);
	}

	private void setUserLocalId(User user) {
		user.setId(id);
		id++;
	}
}