package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.impl.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.impl.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserServiceImpl.class, UserDbStorage.class, FriendsDbStorage.class})
class UserServiceTests {
	private final UserService userService;

	User user1 = new User(0, "Hello@mail.ru", "loginTom", "Thomas", LocalDate.of(2002, 2, 28));
	User user3 = new User(0, "World@yandex.ru", "loginSam", "Samuel", LocalDate.of(2001, 1, 18));

	@DisplayName("Проверка возвращения пустого списка")
	@Test
	public void testFindAllIsEmpty() {
		List<User> users = userService.findAll();
		assertThat(users.isEmpty()).isTrue();
	}

	@DisplayName("Проверка добавления пользователя в базу")
	@Test
	public void testCreate() {
		User user = userService.create(user1);
		User user2 = userService.create(user3);
		assertThat(user).isEqualTo(user1);
		assertThat(user2).isEqualTo(user3);
	}

	@DisplayName("Проверка получения пользователя из базы")
	@Test
	public void testGet() {
		User user11 = userService.create(user1);
		User user33 = userService.create(user3);
		User user = userService.get(user11.getId());
		User user2 = userService.get(user33.getId());
		assertThat(user).isEqualTo(user11);
		assertThat(user2).isEqualTo(user33);
	}

	@DisplayName("Проверка получения списка пользователей из базы")
	@Test
	public void testFindAll() {
		testCreate();
		List<User> users = userService.findAll();
		assertThat(users.size() == 2).isTrue();
	}

	@DisplayName("Проверка обновления пользователя")
	@Test
	public void testUpdate() {
		testCreate();
		User user = userService.findAll().get(0);
		User userToUpdate = new User(user.getId(), "NewHello@mail.ru", "NewloginTom",
				"ThomasMraz", LocalDate.of(2002, 12, 18));
		User userFromUpdate = userService.update(userToUpdate);
		assertThat(userToUpdate).isEqualTo(userFromUpdate);
	}

	@DisplayName("По ТЗ практикум: Проверка одностороннего добавления в друзья, " +
			"при добавлении пользователя 1 к пользователю 2," +
			"пользователь 1 появляется в списке друзей у 2," +
			"но при этом пользователь 2 не добавляется к пользователю 1." +
			"По факту наоборот")
	@Test
	public void testAddFriends() {
		testCreate();
		User user = userService.findAll().get(0);
		User user2 = userService.findAll().get(1);
		List<User> users = userService.addFriend(user.getId(), user2.getId());
		assertThat(users.contains(user2)).isTrue();
	}

	@DisplayName("Удаление пользователя из списка друзей")
	@Test
	public void testRemoveFriends() {
		testCreate();
		User user21 = new User(0, "What@mail.ru", "loginWill", "Willsoun", LocalDate.of(2001, 1, 21));
		User user = userService.findAll().get(0);
		User user2 = userService.findAll().get(1);
		User user4 = userService.create(user21);
		int userId = user.getId();
		int user2Id = user2.getId();
		userService.addFriend(userId, user2Id);
		userService.addFriend(userId, user4.getId());
		List<User> users = userService.removeFriend(userId, user2Id);
		assertThat(users.contains(user4)).isTrue();
		assertThat(users.size() == 1).isTrue();
	}

	@DisplayName("Получение списка друзей")
	@Test
	public void testGetFriends() {
		testCreate();
		User user21 = new User(0, "What@mail.ru", "loginWill", "Willsoun", LocalDate.of(2001, 1, 21));
		User user = userService.findAll().get(0);
		User user2 = userService.findAll().get(1);
		User user4 = userService.create(user21);
		int userId = user.getId();
		int user2Id = user2.getId();
		userService.addFriend(userId, user2Id);
		userService.addFriend(userId, user4.getId());
		List<User> friends = userService.getFriends(userId);
		assertThat(friends.size() == 2).isTrue();
		assertThat(friends.contains(user4)).isTrue();
		assertThat(friends.contains(user2)).isTrue();
	}

	@DisplayName("Получение общего списка друзей")
	@Test
	public void testGetCommonFriends() {
		User user2 = new User(0, "What@mail.ru", "loginWill", "Willsoun", LocalDate.of(2001, 1, 21));
		User user11 = userService.create(user1);
		User user12 = userService.create(user2);
		User user13 = userService.create(user3);

		User user4 = new User(0, "4.21@mail.ru", "login21", "Will21", LocalDate.of(2001, 1, 11));
		User user5 = new User(0, "5.22@mail.ru", "login22", "Will22", LocalDate.of(2000, 2, 12));
		User user6 = new User(0, "6.23@mail.ru", "login23", "Will23", LocalDate.of(1999, 3, 30));
		User user21 = userService.create(user4);
		User user22 = userService.create(user5);
		User user23 = userService.create(user6);

		userService.addFriend(user11.getId(), user12.getId());
		userService.addFriend(user11.getId(), user21.getId());
		userService.addFriend(user11.getId(), user22.getId());
		userService.addFriend(user11.getId(), user23.getId());

		userService.addFriend(user21.getId(), user11.getId());
		userService.addFriend(user21.getId(), user13.getId());
		userService.addFriend(user21.getId(), user22.getId());
		userService.addFriend(user21.getId(), user23.getId());

		List<User> commonFriends = userService.getCommonFriends(user11.getId(), user21.getId());
		assertThat(commonFriends.size() == 2).isTrue();
		assertThat(commonFriends.contains(user22)).isTrue();
		assertThat(commonFriends.contains(user23)).isTrue();
	}
}