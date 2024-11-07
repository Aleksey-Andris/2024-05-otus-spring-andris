package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import ru.otus.hw.dto.CommentDTO;
import ru.otus.hw.writers.AuthorJdbcCustomWriter;
import ru.otus.hw.writers.BookJdbcCustomWriter;
import ru.otus.hw.writers.GenreJdbcCustomWriter;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class BatchWriterConfig {

    private final DataSource dataSource;

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Bean
    public AuthorJdbcCustomWriter authorWriter() {
        return new AuthorJdbcCustomWriter(namedParameterJdbcOperations);
    }

    @Bean
    public BookJdbcCustomWriter bookWriter() {
        return new BookJdbcCustomWriter(namedParameterJdbcOperations);
    }

    @Bean
    public GenreJdbcCustomWriter genreWriter() {
        return new GenreJdbcCustomWriter(namedParameterJdbcOperations);
    }

    @Bean
    public JdbcBatchItemWriter<CommentDTO> commentWriter() {
        JdbcBatchItemWriter<CommentDTO> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("""
                INSERT INTO comments (content, book_id)
                VALUES (:content, (SELECT bit.id_inner FROM book_ids_temp bit WHERE bit.id_external = :bookId))
                """);
        writer.setDataSource(dataSource);
        return writer;
    }

}
