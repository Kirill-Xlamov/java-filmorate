package ru.yandex.practicum.filmorate.storage;

import java.util.Set;

public interface FriendStorage {
	int addFriend(int userId, int friendId);

	boolean removeFriend(int userId, int friendId);

	Set<Integer> getFriends(int userId);
}