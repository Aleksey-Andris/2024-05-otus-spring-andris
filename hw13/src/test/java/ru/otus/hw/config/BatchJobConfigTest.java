package ru.otus.hw.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
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

    private static List<BookJpa> dbBooks;

    private static List<AuthorJpa> dbAuthors;

    private static List<GenreJpa> dbGenres;

    private static List<CommentJpa> dbComments;

    @Autowired
    private CommentRepositoryJpa commentRepositoryJpa;

    @BeforeAll
    static void setUp() {
        dbAuthors = createDbAuthors();
        dbGenres = createDbGenres();
        dbBooks = createDbBooks(dbAuthors, dbGenres);
        dbComments = createDbComments(dbBooks);
    }

    @DisplayName("должен сверять список всех авторов")
    @Test
    void shouldCheckAuthors() {
        var expectedAuthors = dbAuthors;
        var actualAuthors = authorRepositoryJpa.findAll();

        assertThat(actualAuthors).containsExactlyElementsOf(expectedAuthors);
    }

    @DisplayName("должен сверять список всех книг")
    @Test
    void shouldCheckBooks() {
        var expectedBooks = dbBooks;
        var actualBooks = bookRepositoryJpa.findAll();

        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);
    }

    @DisplayName("должен сверять список всех жанров")
    @Test
    void shouldCheckGenres() {
        var expectedGenres = dbGenres;
        var actualGenres = genreRepositoryJpa.findAll();

        assertThat(actualGenres).containsExactlyElementsOf(expectedGenres);
    }

    @DisplayName("должен сверять список всех комментариев")
    @Test
    void shouldCheckComment() {
        var expectedComments = dbComments;
        var actualComments = commentRepositoryJpa.findAll();

        assertThat(actualComments).containsExactlyElementsOf(expectedComments);
    }

    @DisplayName("должен сверять связи комментариев с книгами")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldCheckCommentAndBookLinks(BookJpa book) {
        var expectedComments = dbComments.stream()
                .filter(comment -> comment.getBook().getId() == book.getId())
                .toList();
        var actualComments = commentRepositoryJpa.findByBookId(book.getId());

        assertThat(expectedComments).containsExactlyElementsOf(actualComments);
    }

    @DisplayName("должен сверять связи книг и авторов")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    @Transactional(readOnly = true)
    void shouldCheckBookAndAuthorLinks(BookJpa book) {
        var expectedAuthor = book.getAuthor();
        var actualAuthor = bookRepositoryJpa.findById(book.getId()).get().getAuthor();

        assertThat(expectedAuthor.getId()).isEqualTo(actualAuthor.getId());
    }

    @DisplayName("должен сверять связи книг и жанров")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    @Transactional(readOnly = true)
    void shouldCheckBookAndGenresLinks(BookJpa book) {
        var expectedGenres = book.getGenres();
        var actualGenres = bookRepositoryJpa.findById(book.getId()).get().getGenres();

        assertThat(expectedGenres).containsExactlyElementsOf(actualGenres);
    }

    private static List<AuthorJpa> createDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new AuthorJpa(id, "Author_" + id))
                .toList();
    }

    private static List<BookJpa> createDbBooks(List<AuthorJpa> dbAuthors, List<GenreJpa> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new BookJpa(id,
                        "BookTitle_" + id,
                        dbAuthors.get(id - 1),
                        dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)
                ))
                .toList();
    }

    private static List<GenreJpa> createDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new GenreJpa(id, "Genre_" + id))
                .toList();
    }

    private static List<CommentJpa> createDbComments(List<BookJpa> books) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new CommentJpa(id,
                        "Comment_" + id,
                        books.get(id - 1)
                ))
                .toList();
    }

    private static List<BookJpa> getDbBooks() {
        return dbBooks;
    }

}