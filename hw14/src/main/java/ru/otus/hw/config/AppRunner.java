package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.otus.hw.service.LifeCycleService;

@Component
@RequiredArgsConstructor
public class AppRunner implements CommandLineRunner {

	private final LifeCycleService lifeCycleService;

	@Override
	public void run(String... args) {
		lifeCycleService.startLifeCycle();
	}

}
