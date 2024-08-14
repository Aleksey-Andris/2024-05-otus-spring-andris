package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с книгами")
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager em;

    private List<Author> dbAuthors;

    private List<Genre> dbGenres;

    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
    }

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectBookById(Book expectedBook) {
        var actualBook = bookRepository.findById(expectedBook.getId());

        assertThat(actualBook).isPresent();
        recursiveComparingBook(actualBook.get(), expectedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = bookRepository.findAll();
        var expectedBooks = dbBooks;

        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var expectedBook = new Book(0, "BookTitle_10500", dbAuthors.get(0),
                List.of(dbGenres.get(0), dbGenres.get(2)));
        var returnedBook = bookRepository.save(expectedBook);
        em.flush();
        em.detach(returnedBook);
        var returnedBookOptional = bookRepository.findById(returnedBook.getId());

        assertThat(returnedBookOptional)
                .isPresent();

        returnedBook = returnedBookOptional.get();

        recursiveComparingBook(returnedBook, expectedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expectedBook = new Book(1L, "BookTitle_10500", dbAuthors.get(2),
                List.of(dbGenres.get(4), dbGenres.get(5)));
        var returnedBookOptional = bookRepository.findById(expectedBook.getId());

        assertThat(returnedBookOptional)
                .isPresent();

        em.detach(returnedBookOptional.get());
        var returnedBook = bookRepository.save(expectedBook);

        em.flush();
        em.detach(returnedBook);
        returnedBookOptional = bookRepository.findById(returnedBook.getId());

        assertThat(returnedBookOptional)
                .isPresent();

        returnedBook = returnedBookOptional.get();

        assertThat(returnedBook.getId())
                .isEqualTo(expectedBook.getId());
        recursiveComparingBook(returnedBook, expectedBook);
    }

    @DisplayName("должен удалять книгу")
    @Test
    void shouldDeleteBook() {
        var book = bookRepository.findById(1L);

        assertThat(book).isPresent();

        em.flush();
        em.detach(book.get());
        bookRepository.delete(book.get());

        assertThat(bookRepository.findById(1L)).isEmpty();
    }

    private void recursiveComparingBook(Book actualBook, Book expectedBook) {
        assertThat(actualBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison()
                .comparingOnlyFields("title")
                .isEqualTo(expectedBook);

        assertThat(actualBook.getAuthor()).isNotNull()
                .usingRecursiveComparison()
                .comparingOnlyFields("id", "fullName")
                .isEqualTo(expectedBook.getAuthor());


        assertThat(actualBook.getGenres()).isNotNull()
                .containsExactlyElementsOf(expectedBook.getGenres());

        assertThat(bookRepository.findById(actualBook.getId()))
                .isPresent()
                .get()
                .isEqualTo(actualBook);
    }

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }

    private static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Book(id,
                        "BookTitle_" + id,
                        dbAuthors.get(id - 1),
                        dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)
                ))
                .toList();
    }

    private static List<Book> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }

}