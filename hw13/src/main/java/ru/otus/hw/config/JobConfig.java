package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.dto.AuthorDTO;
import ru.otus.hw.dto.GenreDTO;
import ru.otus.hw.models.mongo.AuthorMongo;
import ru.otus.hw.models.mongo.BookMongo;
import ru.otus.hw.models.mongo.CommentMongo;
import ru.otus.hw.models.mongo.GenreMongo;
import ru.otus.hw.repositories.jpa.AuthorRepositoryJpa;
import ru.otus.hw.repositories.jpa.BookRepositoryJpa;
import ru.otus.hw.repositories.jpa.CommentRepositoryJpa;
import ru.otus.hw.repositories.jpa.GenreRepositoryJpa;
import ru.otus.hw.repositories.mongo.AuthorRepositoryMongo;
import ru.otus.hw.repositories.mongo.BookRepositoryMongo;
import ru.otus.hw.repositories.mongo.CommentRepositoryMongo;
import ru.otus.hw.repositories.mongo.GenreRepositoryMongo;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class JobConfig {

    private static final String FIND_ALL = "findAll";

    private final DataSource dataSource;

    private final JdbcTemplate jdbcTemplate;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final AuthorRepositoryMongo authorRepositoryMongo;

    private final BookRepositoryMongo bookRepositoryMongo;

    private final CommentRepositoryMongo commentRepositoryMongo;

    private final GenreRepositoryMongo genreRepositoryMongo;

    private final AuthorRepositoryJpa authorRepositoryJpa;

    private final BookRepositoryJpa bookRepositoryJpa;

    private final CommentRepositoryJpa commentRepositoryJpa;

    private final GenreRepositoryJpa genreRepositoryJpa;



/*TODO
    1) Создание временных таблиц -  ОК
    2) Вставка жанров и авторов
    3) Вставка книг
    4) Вставка комментариев связь жанров
    6) Удаление временных таблиц - ОК
*/

    //TODO
    @Bean
    public RepositoryItemReader<AuthorMongo> authorReader() {
        return new RepositoryItemReaderBuilder<AuthorMongo>()
                .name("authorReader")
                .repository(authorRepositoryMongo)
                .methodName(FIND_ALL)
                .build();
    }

    @Bean
    public RepositoryItemReader<BookMongo> bookReader() {
        return new RepositoryItemReaderBuilder<BookMongo>()
                .name("bookReader")
                .repository(bookRepositoryJpa)
                .methodName(FIND_ALL)
                .build();
    }

    @Bean
    public RepositoryItemReader<CommentMongo> commentReader() {
        return new RepositoryItemReaderBuilder<CommentMongo>()
                .name("commentReader")
                .repository(commentRepositoryJpa)
                .methodName(FIND_ALL)
                .build();
    }

    @Bean
    public RepositoryItemReader<GenreMongo> genreReader() {
        return new RepositoryItemReaderBuilder<GenreMongo>()
                .name("genreReader")
                .repository(genreRepositoryJpa)
                .methodName(FIND_ALL)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<AuthorDTO> authorJdbcBatchItemWriter() {
        JdbcBatchItemWriter<AuthorDTO> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql(""" 
                        DECLARE
                            insertedId BIGINT;
                        BEGIN
                            INSERT INTO authors (full_name) VALUES (:fullName) RETURNING id INTO insertedId;
                            INSERT INTO author_ids_temp (id_inner, id_external)  VALUES (insertedId, :id);
                        END;
                        """);
        writer.setDataSource(dataSource);
        return writer;
    }

    @Bean
    public JdbcBatchItemWriter<GenreDTO> genreJdbcBatchItemWriter() {
        JdbcBatchItemWriter<GenreDTO> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql(""" 
                        DECLARE
                            insertedId BIGINT;
                        BEGIN
                            INSERT INTO genres (name) VALUES (:fullName) RETURNING id INTO insertedId;
                            INSERT INTO genre_ids_temp (id_inner, id_external)  VALUES (insertedId, :id);
                        END;
                        """);
        writer.setDataSource(dataSource);
        return writer;
    }

    @Bean
    public Job migrateMongoToRelationDbJob(Flow createTempTablesFlow, Flow dropTempTablesFlow) {
        return new JobBuilder("migrateMongoToRelationDbJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(createTempTablesFlow)
                .next(authorAndGenreMigrationFlow)
                .next(bookMigrationStep)
                .next(commentMigrationStep)
                .next(dropTempTablesFlow)
                .build();
    }

    @Bean
    public Flow createTempTablesFlow(Step createAuthorIdsTempTableStep, Step createGenreIdsTempTableStep,
                                     Step createBookIdsTempTableStep) {
        return new FlowBuilder<SimpleFlow>("createTempTablesFlow")
                .start(createAuthorIdsTempTableStep)
                .next(createGenreIdsTempTableStep)
                .next(createBookIdsTempTableStep)
                .build();
    }

    @Bean
    public Step createAuthorIdsTempTableStep() {
        return new StepBuilder("createAuthorIdsTempTableStep", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    jdbcTemplate.execute("CREATE TABLE author_ids_temp (" +
                            "id_inner BIGINT NOT NULL, " +
                            "id_external VARCHAR(255) NOT NULL)");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public Step createGenreIdsTempTableStep() {
        return new StepBuilder("createBooksIdTempTableStep", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    jdbcTemplate.execute("CREATE TABLE genre_ids_temp (" +
                            "id_inner BIGINT NOT NULL, " +
                            "id_external VARCHAR(255) NOT NULL)");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }


    @Bean
    public Step createBookIdsTempTableStep() {
        return new StepBuilder("createBooksIdTempTableStep", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    jdbcTemplate.execute("CREATE TABLE books_ids_temp (" +
                            "id_inner BIGINT NOT NULL, " +
                            "id_external VARCHAR(255) NOT NULL)");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public Flow dropTempTablesFlow(Step dropAuthorIdsTempTableStep, Step dropGenreIdsTempTableStep,
                                   Step dropBookIdsTempTableStep) {
        return new FlowBuilder<SimpleFlow>("dropTempTablesFlow")
                .start(dropAuthorIdsTempTableStep)
                .next(dropGenreIdsTempTableStep)
                .next(dropBookIdsTempTableStep)
                .build();
    }

    @Bean
    public Step dropAuthorIdsTempTableStep() {
        return new StepBuilder("dropAuthorIdsTempTableStep", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    jdbcTemplate.execute("DROP TABLE author_ids_temp");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public Step dropGenreIdsTempTableStep() {
        return new StepBuilder("dropGenreIdsTempTableStep", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    jdbcTemplate.execute("DROP TABLE genre_ids_temp");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }


    @Bean
    public Step dropBookIdsTempTableStep() {
        return new StepBuilder("dropBookIdsTempTableStep", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    jdbcTemplate.execute("DROP TABLE books_ids_temp");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public TaskExecutor asynTaskExecutor() {
        return new SimpleAsyncTaskExecutor("spring_batch_async_task_executor");
    }


}
