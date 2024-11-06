package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
	private final UserStorage userStorage;
	private final FriendStorage friendStorage;

	@Autowired
	public UserServiceImpl(@Qualifier("userDbStorage") UserStorage userStorage, FriendStorage friendStorage) {
		this.userStorage = userStorage;
		this.friendStorage = friendStorage;
	}

	@Override
	public List<User> addFriend(int userId, int friendId) {
		checkUser(userId);
		checkUser(friendId);
		int receivedUserId = friendStorage.addFriend(userId, friendId);
		log.info("К пользователю id:{}, добавлен друг id:{}", receivedUserId, friendId);
		return getFriends(userId);
	}

	@Override
	public List<User> removeFriend(int userId, int friendId) {
		checkUser(userId);
		checkUser(friendId);
		if (!friendStorage.removeFriend(userId, friendId)) {
			log.info("У пользователя id:{} не было в друзьях id:{}", userId, friendId);
		}
		log.info("У пользователя id:{}, удален друг id:{}", userId, friendId);
		return getFriends(userId);
	}

	@Override
	public List<User> getFriends(int userId) {
		checkUser(userId);
		Set<Integer> friends = friendStorage.getFriends(userId);
		if (friends.isEmpty()) {
			log.info("У пользователя id:{} нет друзей", userId);
			return Collections.emptyList();
		}
		log.info("У пользователя id:{} получен список друзей", userId);
		return friends.stream()
				.map(userStorage::get)
				.collect(Collectors.toList());
	}

	@Override
	public List<User> getCommonFriends(int userId, int otherId) {
		List<User> friends = getFriends(userId);
		List<User> otherFriends = getFriends(otherId);
		List<User> commonFriends = friends.stream()
				.filter(otherFriends::contains)
				.collect(Collectors.toList());
		log.info("У пользователей id:{} и id:{} общих друзей:{}", userId, otherId, commonFriends.size());
		return commonFriends;
	}

	@Override
	public List<User> findAll() {
		List<User> users = userStorage.findAll();
		log.info("Количество найденных пользователей {}", users.size());
		return users;
	}

	@Override
	public User create(User user) {
		if (user.getId() != 0) {
			throw new ValidationException("При добавлении id должен быть 0");
		}
		String userLogin = user.getLogin();
		if (user.getName() == null || user.getName().isBlank()) {
			log.info("Установлено имя {}", userLogin);
			user.setName(userLogin);
		}
		User userAdded = userStorage.create(user);
		log.info("Добавлен новый пользователь {}", userAdded);
		return userAdded;
	}

	@Override
	public User update(User user) {
		int userId = user.getId();
		checkUser(userId);
		if (userId == 0) {
			throw new ObjectNotFoundException("Введите пользователя, которого надо обновить");
		}
		User userUpdated = userStorage.update(user);
		log.info("Пользователь обновлен: {}", userUpdated);
		return userUpdated;
	}

	@Override
	public User get(int id) {
		User user = checkUser(id);
		log.info("Получен пользователь с id: {}", id);
		return user;
	}

	private User checkUser(int id) {
		User user = userStorage.get(id);
		if (user == null) {
			throw new ObjectNotFoundException(String.format("Пользователь с id=%s не найден", id));
		}
		return user;
	}
}