package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent(value = "Testing run commands")
@RequiredArgsConstructor
public class ApplicationRun {

    private final TestRunnerService testRunnerService;

    @ShellMethod(value = "Run Testing", key = {"run", "start", "r", "s"})
    public void runTest() {
      testRunnerService.run();
    }

}
