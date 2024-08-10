package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDTO;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookToStringConverter {

    private final AuthorToStringConverter authorToStringConverter;

    private final GenreToStringConverter genreToStringConverter;

    public String bookToString(BookDTO book) {
        var genresString = book.getGenres().stream()
                .map(genreToStringConverter::genreToString)
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));
        return "Id: %d, title: %s, author: {%s}, genres: [%s]".formatted(
                book.getId(),
                book.getTitle(),
                authorToStringConverter.authorToString(book.getAuthor()),
                genresString);
    }

}
