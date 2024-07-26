package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final GenreRepository genreRepository;

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public Optional<Book> findById(long id) {
        return Optional.ofNullable(namedParameterJdbcOperations.query("SELECT " +
                "b.id AS id, " +
                "b.title AS title, " +
                "a.id AS author_id,  " +
                "a.full_name AS author_full_name, " +
                "g.id AS genre_id, " +
                "g.name AS genre_name, " +
                "a.full_name AS author_full_name " +
                "FROM books b " +
                "INNER JOIN authors a ON b.author_id = a.id " +
                "INNER JOIN books_genres bg on b.id = bg.book_id " +
                "INNER JOIN genres g on bg.genre_id = g.id " +
                "WHERE b.id = :id ", Map.of("id", id), new BookResultSetExtractor()));
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        namedParameterJdbcOperations.update("DELETE FROM books WHERE id = :id", Map.of("id", id));
    }

    private List<Book> getAllBooksWithoutGenres() {
        return namedParameterJdbcOperations.query("SELECT " +
                "b.id AS id, " +
                "b.title AS title, " +
                "a.id AS author_id,  " +
                "a.full_name AS author_full_name " +
                "FROM books b " +
                "INNER JOIN authors a ON b.author_id = a.id", new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return namedParameterJdbcOperations.query("SELECT book_id, genre_id FROM books_genres",
                (rs, i) -> new BookGenreRelation(rs.getLong("book_id"), rs.getLong("genre_id")));
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        var booksMap = booksWithoutGenres.stream().collect(Collectors.toMap(Book::getId, book -> book));
        var genresMap = genres.stream().collect(Collectors.toMap(Genre::getId, genre -> genre));
        for (var relation : relations) {
            var book = booksMap.get(relation.bookId);
            var genre = genresMap.get(relation.genreId);
            book.getGenres().add(genre);
        }
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        var params = new MapSqlParameterSource(Map.of(
                "title", book.getTitle(),
                "author_id", book.getAuthor().getId()));
        namedParameterJdbcOperations.update("INSERT INTO books(title, author_id) VALUES(:title, :author_id)", params,
                keyHolder);
        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        int updatedRowsCount = namedParameterJdbcOperations.update(
                "UPDATE books SET  title = :title, author_id = :author_id WHERE id = :id",
                Map.of("title", book.getTitle(),
                        "author_id", book.getAuthor().getId(),
                        "id", book.getId()));
        if (updatedRowsCount == 0) {
            throw new EntityNotFoundException(String.format("Book with id: %s not found", book.getId()));
        }
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        var bathParams = book.getGenres().stream()
                .map(genre -> new MapSqlParameterSource(Map.of(
                        "book_id", book.getId(),
                        "genre_id", genre.getId())))
                .toArray(MapSqlParameterSource[]::new);
        namedParameterJdbcOperations.batchUpdate(
                "INSERT INTO books_genres(book_id, genre_id) VALUES (:book_id, :genre_id)", bathParams);
    }

    private void removeGenresRelationsFor(Book book) {
        namedParameterJdbcOperations.update(
                "DELETE FROM books_genres WHERE book_id = :book_id", Map.of("book_id", book.getId()));
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            var author = new Author(rs.getLong("author_id"), rs.getString("author_full_name"));
            return new Book(rs.getLong("id"), rs.getString("title"), author, new LinkedList<>());
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            Book book = null;
            while (rs.next()) {
                if (book == null) {
                    book = new BookRowMapper().mapRow(rs, rs.getRow());
                }
                var genre = new Genre(rs.getLong("genre_id"), rs.getString("genre_name"));
                book.getGenres().add(genre);
            }
            return book;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }

}
