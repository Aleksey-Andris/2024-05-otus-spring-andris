package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("Репозиторий для работы с комментариями")
@DataMongoTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    private List<Comment> dbComment;

    private List<Author> dbAuthors;

    private List<Genre> dbGenres;

    private List<Book> dbBooks;

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
    void shouldReturnCorrectCommentById(Comment expectedComment) {
        var actualComment = commentRepository.findById(expectedComment.getId());

        assertThat(actualComment).isPresent();
        recursiveComparingComment(actualComment.get(), expectedComment);
    }

    @DisplayName("должен загружать комментарии по книге")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectCommentsByBook(Book book) {
        var expectedComments = dbComment.stream()
                .filter(comment -> Objects.equals(comment.getBook().getId(), book.getId()))
                .toList();
        var actualComments = commentRepository.findByBookId(book.getId());

        assertThat(expectedComments).containsExactlyElementsOf(actualComments);
    }

    @DisplayName("должен сохранять новый комментарий")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void shouldSaveUpdatedBookComment() {
        var expectedComment = new Comment(null, "Comment_100500", dbBooks.get(0));
        var returnedComment = commentRepository.save(expectedComment);

        recursiveComparingComment(returnedComment, expectedComment);
    }

    @DisplayName("должен сохранять измененный комментарий")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void save() {
        var expectedComment = new Comment("1", "Comment_100500", dbBooks.get(1));
        var returnedCommentOptional = commentRepository.findById(expectedComment.getId());

        assertThat(returnedCommentOptional)
                .isPresent();

        var returnedComment = commentRepository.save(expectedComment);

        assertThat(returnedComment.getId())
                .isEqualTo(expectedComment.getId());
        recursiveComparingComment(returnedComment, expectedComment);
    }

    @DisplayName("должен удалять комментарий")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void delete() {
        var comment = commentRepository.findById("1");

        assertThat(comment).isPresent();

        commentRepository.delete(comment.get());

        assertThat(commentRepository.findById("1")).isEmpty();
    }

    private void recursiveComparingComment(Comment actualComment, Comment expectedComment) {
        assertThat(actualComment).isNotNull()
                .matches(book -> book.getId() != null)
                .usingRecursiveComparison()
                .comparingOnlyFields("content")
                .isEqualTo(expectedComment);

        var actualBook = actualComment.getBook();
        var expectedBook = expectedComment.getBook();

        assertThat(actualBook).isNotNull()
                .usingRecursiveComparison()
                .comparingOnlyFields("id", "title", "author")
                .isEqualTo(expectedBook);

         assertThat(actualBook.getGenres()).containsExactlyElementsOf(expectedBook.getGenres());
    }

    private static List<Comment> getDbComments() {
        var books = getDbBooks();
        return getDbComments(books);
    }

    private static List<Comment> getDbComments(List<Book> books) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Comment(String.format("%s", id),
                        "Comment_" + id,
                        books.get(id - 1)
                ))
                .toList();
    }

    private static List<Book> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }


    private static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Book(String.format("%s", id),
                        "BookTitle_" + id,
                        dbAuthors.get(id - 1),
                        dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)
                ))
                .toList();
    }

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(String.format("%s", id), "Author_" + id))
                .toList();
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(String.format("%s", id), "Genre_" + id))
                .toList();
    }

}