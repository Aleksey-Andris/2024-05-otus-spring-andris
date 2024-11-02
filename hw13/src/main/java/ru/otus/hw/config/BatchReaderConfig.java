package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.AuthorMongo;
import ru.otus.hw.models.mongo.BookMongo;
import ru.otus.hw.models.mongo.CommentMongo;
import ru.otus.hw.models.mongo.GenreMongo;
import ru.otus.hw.repositories.mongo.AuthorRepositoryMongo;
import ru.otus.hw.repositories.mongo.BookRepositoryMongo;
import ru.otus.hw.repositories.mongo.CommentRepositoryMongo;
import ru.otus.hw.repositories.mongo.GenreRepositoryMongo;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class BatchReaderConfig {

    private static final String FIND_ALL = "findAll";

    private final AuthorRepositoryMongo authorRepositoryMongo;

    private final BookRepositoryMongo bookRepositoryMongo;

    private final CommentRepositoryMongo commentRepositoryMongo;

    private final GenreRepositoryMongo genreRepositoryMongo;

    @Bean
    public RepositoryItemReader<AuthorMongo> authorReader() {
        return new RepositoryItemReaderBuilder<AuthorMongo>()
                .name("authorReader")
                .repository(authorRepositoryMongo)
                .methodName(FIND_ALL)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public RepositoryItemReader<BookMongo> bookReader() {
        return new RepositoryItemReaderBuilder<BookMongo>()
                .name("bookReader")
                .repository(bookRepositoryMongo)
                .methodName(FIND_ALL)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public RepositoryItemReader<GenreMongo> genreReader() {
        return new RepositoryItemReaderBuilder<GenreMongo>()
                .name("genreReader")
                .repository(genreRepositoryMongo)
                .methodName(FIND_ALL)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public RepositoryItemReader<CommentMongo> commentReader() {
        return new RepositoryItemReaderBuilder<CommentMongo>()
                .name("commentReader")
                .repository(commentRepositoryMongo)
                .methodName(FIND_ALL)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

}
