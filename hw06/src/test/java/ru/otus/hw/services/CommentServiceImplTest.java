package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.CommentConverterImpl;
import ru.otus.hw.dto.AuthorDTO;
import ru.otus.hw.dto.BookDTO;
import ru.otus.hw.dto.CommentDTO;
import ru.otus.hw.dto.GenreDTO;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaCommentRepository;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("Сервис для работы с комментариями")
@DataJpaTest
@Import({CommentServiceImpl.class, CommentConverterImpl.class,
        JpaBookRepository.class, JpaCommentRepository.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class CommentServiceImplTest {

    @Autowired
    private CommentServiceImpl serviceTest;

    private List<CommentDTO> dbComment;

    private List<AuthorDTO> dbAuthors;

    private List<GenreDTO> dbGenres;

    private List<BookDTO> dbBooks;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
        dbComment = getDbComments(dbBooks);
    }

    @DisplayName("должен загружать комментарий по id")
    @ParameterizedTest
    @MethodSource("getDbComments")
    void shouldReturnCorrectCommentById(CommentDTO expectedComment) {
        var actualComment = serviceTest.findById(expectedComment.getId());
        assertThat(actualComment).isPresent();
        assertThat(actualComment)
                .isNotNull()
                .isPresent()
                .get()
                .isEqualTo(expectedComment);
    }

    @DisplayName("должен загружать комментарии по книге")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectCommentsByBook(BookDTO book) {
        var expectedComments = dbComment.stream()
                .filter(comment -> comment.getBook().getId() == book.getId())
                .toList();
        var actualComments = serviceTest.findByBookId(book.getId());

        assertThat(expectedComments).containsExactlyElementsOf(actualComments);
    }

    @DisplayName("должен сохранять новый комментарий")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveUpdatedBookComment() {
        var expectedComment = new CommentDTO(0, "Comment_100500", dbBooks.get(0));
        var returnedComment = serviceTest.insert(expectedComment.getContent(),
                expectedComment.getBook().getId());
        assertThat(returnedComment)
                .isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedComment);
    }

    @DisplayName("должен сохранять измененный комментарий")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void save() {
        var expectedComment = new CommentDTO(1, "Comment_100500", dbBooks.get(1));

        assertThat(serviceTest.findById(expectedComment.getId()))
                .isPresent();

        var returnedComment = serviceTest.update(expectedComment.getId(),
                expectedComment.getContent(),
                expectedComment.getBook().getId());

        assertThat(returnedComment)
                .isNotNull()
                .isEqualTo(expectedComment);
    }

    @DisplayName("должен удалять комментарий")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void delete() {
        var comment = serviceTest.findById(1L);
        assertThat(comment).isPresent();
        serviceTest.deleteById(comment.get().getId());
        assertThat(serviceTest.findById(1L)).isEmpty();
    }

    private static List<CommentDTO> getDbComments() {
        var books = getDbBooks();
        return getDbComments(books);
    }

    private static List<CommentDTO> getDbComments(List<BookDTO> books) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new CommentDTO(id,
                        "Comment_" + id,
                        books.get(id - 1)
                ))
                .toList();
    }

    private static List<BookDTO> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }


    private static List<BookDTO> getDbBooks(List<AuthorDTO> dbAuthors, List<GenreDTO> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new BookDTO(id,
                        "BookTitle_" + id,
                        dbAuthors.get(id - 1),
                        dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)
                ))
                .toList();
    }

    private static List<AuthorDTO> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new AuthorDTO(id, "Author_" + id))
                .toList();
    }

    private static List<GenreDTO> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new GenreDTO(id, "Genre_" + id))
                .toList();
    }

}