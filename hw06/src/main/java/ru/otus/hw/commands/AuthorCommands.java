package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.AuthorToStringConverter;
import ru.otus.hw.services.AuthorService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class AuthorCommands {

    private final AuthorService authorService;

    private final AuthorToStringConverter authorToStringConverter;

    @ShellMethod(value = "Find all authors", key = "aa")
    public String findAllAuthors() {
        return authorService.findAll().stream()
                .map(authorToStringConverter::authorToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

}
