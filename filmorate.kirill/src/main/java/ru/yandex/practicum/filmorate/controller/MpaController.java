package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaController {
	private final MpaService mpaService;

	public MpaController(MpaService mpaService) {
		this.mpaService = mpaService;
	}

	@GetMapping
	public List<Mpa> getMpa() {
		return mpaService.getMpa();
	}

	@GetMapping("/{mpaId}")
	public Mpa getMpaById(@PathVariable int mpaId) {
		return mpaService.getMpaById(mpaId);
	}
}