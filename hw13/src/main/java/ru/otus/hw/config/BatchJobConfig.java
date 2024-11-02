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
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.dto.AuthorDTO;
import ru.otus.hw.dto.BookDTO;
import ru.otus.hw.dto.CommentDTO;
import ru.otus.hw.dto.GenreDTO;
import ru.otus.hw.models.mongo.AuthorMongo;
import ru.otus.hw.models.mongo.BookMongo;
import ru.otus.hw.models.mongo.CommentMongo;
import ru.otus.hw.models.mongo.GenreMongo;
import ru.otus.hw.processor.AuthorProcessor;
import ru.otus.hw.processor.BookProcessor;
import ru.otus.hw.processor.CommentProcessor;
import ru.otus.hw.processor.GenreProcessor;
import ru.otus.hw.writers.AuthorJdbcCustomWriter;
import ru.otus.hw.writers.BookJdbcCustomWriter;
import ru.otus.hw.writers.GenreJdbcCustomWriter;

@Configuration
@RequiredArgsConstructor
public class BatchJobConfig {

    private static final int CHUNK_SIZE = 5;

    private final JdbcTemplate jdbcTemplate;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final AuthorProcessor authorProcessor;

    private final BookProcessor bookProcessor;

    private final GenreProcessor genreProcessor;

    private final CommentProcessor commentProcessor;

    private final RepositoryItemReader<AuthorMongo> authorReader;

    private final RepositoryItemReader<BookMongo> bookReader;

    private final RepositoryItemReader<GenreMongo> genreReader;

    private final RepositoryItemReader<CommentMongo> commentReader;

    private final AuthorJdbcCustomWriter authorWriter;

    private final BookJdbcCustomWriter bookWriter;

    private final GenreJdbcCustomWriter genreWriter;

    private final JdbcBatchItemWriter<CommentDTO> commentWriter;

    @Bean
    public Job migrateMongoToRelationDbJob(Flow createTempTablesFlow, Flow authorAndGenreMigrationFlow,
                                           Flow dropTempTablesFlow, Step bookMigrationStep, Step commentMigrationStep) {
        return new JobBuilder("migrateMongoToRelationDbJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(createTempTablesFlow)
                .next(authorAndGenreMigrationFlow)
                .next(bookMigrationStep)
                .next(commentMigrationStep)
                .next(dropTempTablesFlow)
                .end()
                .build();
    }

    @Bean
    public TaskExecutor asynTaskExecutor() {
        return new SimpleAsyncTaskExecutor("spring_batch_async_task_executor");
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
                    jdbcTemplate.execute("""
                            CREATE TABLE author_ids_temp (
                                id_inner BIGINT NOT NULL,
                                id_external VARCHAR(255) NOT NULL)
                            """);
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public Step createBookIdsTempTableStep() {
        return new StepBuilder("createBooksIdTempTableStep", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    jdbcTemplate.execute("""
                            CREATE TABLE book_ids_temp (
                                id_inner BIGINT NOT NULL,
                                id_external VARCHAR(255) NOT NULL)
                            """);
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public Step createGenreIdsTempTableStep() {
        return new StepBuilder("createBooksIdTempTableStep", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    jdbcTemplate.execute("""
                            CREATE TABLE genre_ids_temp (
                                id_inner BIGINT NOT NULL,
                                id_external VARCHAR(255) NOT NULL)
                            """);
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public Flow authorAndGenreMigrationFlow(TaskExecutor asynTaskExecutor, Flow authorMigrationFlow,
                                            Flow genreMigrationFlow) {
        return new FlowBuilder<SimpleFlow>("authorAndGenreMigrationFlow")
                .split(asynTaskExecutor)
                .add(authorMigrationFlow, genreMigrationFlow)
                .build();
    }

    @Bean
    public Flow authorMigrationFlow(Step authorMigrationStep) {
        return new FlowBuilder<SimpleFlow>("authorMigrationFlow")
                .start(authorMigrationStep)
                .build();
    }

    @Bean
    public Flow genreMigrationFlow(Step genreMigrationStep) {
        return new FlowBuilder<SimpleFlow>("genreMigrationFlow")
                .start(genreMigrationStep)
                .build();
    }

    @Bean
    public Step authorMigrationStep() {
        return new StepBuilder("authorMigrationStep", jobRepository)
                .<AuthorMongo, AuthorDTO>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(authorReader)
                .processor(authorProcessor)
                .writer(authorWriter)
                .build();
    }

    @Bean
    public Step bookMigrationStep() {
        return new StepBuilder("bookMigrationStep", jobRepository)
                .<BookMongo, BookDTO>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(bookReader)
                .processor(bookProcessor)
                .writer(bookWriter)
                .build();
    }

    @Bean
    public Step genreMigrationStep() {
        return new StepBuilder("genreMigrationStep", jobRepository)
                .<GenreMongo, GenreDTO>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(genreReader)
                .processor(genreProcessor)
                .writer(genreWriter)
                .build();
    }

    @Bean
    public Step commentMigrationStep() {
        return new StepBuilder("commentMigrationStep", jobRepository)
                .<CommentMongo, CommentDTO>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(commentReader)
                .processor(commentProcessor)
                .writer(commentWriter)
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
                    jdbcTemplate.execute("DROP TABLE book_ids_temp");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

}
