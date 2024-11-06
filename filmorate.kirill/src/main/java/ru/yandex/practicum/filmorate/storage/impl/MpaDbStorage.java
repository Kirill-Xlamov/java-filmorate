package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Repository
public class MpaDbStorage implements MpaStorage {
	private static final String SQL_QUERY_GET_BY_ID = "select * from mpa where mpa_id = ?";
	private static final String SQL_QUERY_GET = "select * from mpa";
	private final JdbcTemplate jdbcTemplate;

	public MpaDbStorage(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<Mpa> getMpa() {
		List<Mpa> mpa = jdbcTemplate.query(SQL_QUERY_GET, (rs, rowNum) -> makeMpa(rs));
		if (mpa.isEmpty()) {
			return Collections.emptyList();
		}
		return mpa;
	}

	@Override
	public Mpa getMpaById(int mpaId) {
		SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(SQL_QUERY_GET_BY_ID, mpaId);
		if (!mpaRows.next()) {
			return null;
		}
		return new Mpa(
				mpaRows.getInt("mpa_id"),
				mpaRows.getString("name"));
	}

	private Mpa makeMpa(ResultSet rs) throws SQLException {
		return new Mpa(rs.getInt("mpa_id"), rs.getString("name"));
	}
}