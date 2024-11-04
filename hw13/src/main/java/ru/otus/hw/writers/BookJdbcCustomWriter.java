package ru.otus.hw.writers;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDTO;
import ru.otus.hw.writers.dto.IdLink;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class BookJdbcCustomWriter implements ItemWriter<BookDTO> {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    public BookJdbcCustomWriter(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    @Override
    public void write(Chunk<? extends BookDTO> chunk) {
        if (chunk.isEmpty()) {
            return;
        }
        List<IdLink> idLinks = saveBooks(chunk.getItems());
        saveIdLinks(idLinks);
        List<BookGenreLink> bookGenreLinks = chunk.getItems()
                .stream()
                .flatMap(book -> book.getGenresIds()
                        .stream()
                        .map(genreId -> new BookGenreLink(book.getId(), genreId))
                ).toList();
        saveGenresLinks(bookGenreLinks);
    }

    private List<IdLink> saveBooks(List<? extends BookDTO> items) {
        var keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcOperations.batchUpdate("""
                INSERT INTO books (title, author_id)
                VALUES (:title, (SELECT ait.id_inner FROM author_ids_temp ait WHERE id_external = :authorId))
                """, SqlParameterSourceUtils.createBatch(items), keyHolder, new String[]{"id"});
        List<IdLink> idLinks = new LinkedList<>();
        int index = 0;
        for (Map<String, Object> map : keyHolder.getKeyList()) {
            idLinks.add(new IdLink((long) map.get("id"), items.get(index).getId()));
            index++;
        }
        return idLinks;
    }

    private void saveIdLinks(List<IdLink> idLinks) {
        namedParameterJdbcOperations.batchUpdate(
                "INSERT INTO book_ids_temp (id_inner, id_external) VALUES (:idInner, :idExternal)",
                SqlParameterSourceUtils.createBatch(idLinks));
    }

    private void saveGenresLinks(List<BookGenreLink> bookGenreLinks) {
        namedParameterJdbcOperations.batchUpdate("""
                INSERT INTO books_genres (book_id, genre_id)
                VALUES ((SELECT bit.id_inner FROM book_ids_temp bit WHERE id_external = :bookId),
                        (SELECT bit.id_inner FROM genre_ids_temp bit WHERE id_external = :genreId))
                """, SqlParameterSourceUtils.createBatch(bookGenreLinks));
    }

    public record BookGenreLink(String bookId, String genreId) {
    }

}
