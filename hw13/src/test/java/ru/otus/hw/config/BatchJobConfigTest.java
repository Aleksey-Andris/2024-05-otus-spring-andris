package ru.otus.hw.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.models.jpa.AuthorJpa;
import ru.otus.hw.models.jpa.BookJpa;
import ru.otus.hw.models.jpa.CommentJpa;
import ru.otus.hw.models.jpa.GenreJpa;
import ru.otus.hw.repositories.jpa.AuthorRepositoryJpa;
import ru.otus.hw.repositories.jpa.BookRepositoryJpa;
import ru.otus.hw.repositories.jpa.CommentRepositoryJpa;
import ru.otus.hw.repositories.jpa.GenreRepositoryJpa;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@SpringBatchTest
@DisplayName("Тест выполнения миграций")
class BatchJobConfigTest {

    @Autowired
    private AuthorRepositoryJpa authorRepositoryJpa;

    @Autowired
    private BookRepositoryJpa bookRepositoryJpa;

    @Autowired
    private GenreRepositoryJpa genreRepositoryJpa;

    private List<BookJpa> dbBooks;

    private List<AuthorJpa> dbAuthors;

    private List<GenreJpa> dbGenres;

    private List<CommentJpa> dbComments;
    @Autowired
    private CommentRepositoryJpa commentRepositoryJpa;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
        dbComments = getDbComments(dbBooks);
    }

    @DisplayName("должен сверять список всех авторов")
    @Test
    void shouldReturnCorrectAuthorsList() {
        var expectedAuthors = dbAuthors;
        var actualAuthors = authorRepositoryJpa.findAll();

        assertThat(actualAuthors).containsExactlyElementsOf(expectedAuthors);
    }

    @DisplayName("должен сверять список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var expectedBooks = dbBooks;
        var actualBooks = bookRepositoryJpa.findAll();

        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);
    }

    @DisplayName("должен сверять список всех жанров")
    @Test
    void shouldReturnCorrectGenresList() {
        var expectedGenres = dbGenres;
        var actualGenres = genreRepositoryJpa.findAll();

        assertThat(actualGenres).containsExactlyElementsOf(expectedGenres);
    }

    @DisplayName("должен сверять список всех комментариев")
    @Test
    void shouldReturnCorrectCommentById() {
        var expectedComments = dbComments;
        var actualComments = commentRepositoryJpa.findAll();

        assertThat(actualComments).containsExactlyElementsOf(expectedComments);
    }

    private static List<AuthorJpa> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new AuthorJpa(id, "Author_" + id))
                .toList();
    }

    private static List<BookJpa> getDbBooks(List<AuthorJpa> dbAuthors, List<GenreJpa> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new BookJpa(id,
                        "BookTitle_" + id,
                        dbAuthors.get(id - 1),
                        dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)
                ))
                .toList();
    }

    private static List<GenreJpa> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new GenreJpa(id, "Genre_" + id))
                .toList();
    }

    private static List<CommentJpa> getDbComments(List<BookJpa> books) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new CommentJpa(id,
                        "Comment_" + id,
                        books.get(id - 1)
                ))
                .toList();
    }

}