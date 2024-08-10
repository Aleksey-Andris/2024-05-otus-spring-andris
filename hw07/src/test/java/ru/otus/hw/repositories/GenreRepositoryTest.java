package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Genre;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с жанрами")
@DataJpaTest
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenresList() {
        var expectedGenres = getDbGenres();
        var actualGenres = genreRepository.findAll();

        assertThat(actualGenres)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(expectedGenres);
    }

    @DisplayName("должен загружать список жанров по списку id")
    @Test
    void shouldReturnCorrectGenresListById() {
        var expectedGenres = getDbGenres().stream().filter(genre -> Set.of(1L, 2L).contains(genre.getId())).toList();
        var actualGenres = genreRepository.findByIdIn(Set.of(1L, 2L));

        assertThat(actualGenres)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(expectedGenres);
    }

    @DisplayName("должен загружать жанр по id")
    @Test
    void shouldReturnCorrectGenreById() {
        var expectedGenres = List.of(new Genre(1, "Genre_1"));
        var actualGenres = genreRepository.findByIdIn(new HashSet<>(List.of(1L)));

        assertThat(actualGenres)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(expectedGenres);
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }

}