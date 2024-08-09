package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на для работы с авторами")
@DataJpaTest
@Import({JpaAuthorRepository.class})
class JpaAuthorRepositoryTest {

    @Autowired
    private JpaAuthorRepository repositoryJdbc;

    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnCorrectAuthorsList() {
        var expectedAuthors = getDbAuthors();
        var actualAuthors = repositoryJdbc.findAll();
        assertThat(actualAuthors)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(expectedAuthors);
    }

    @DisplayName("должен загружать автора по id")
    @Test
    void shouldReturnCorrectAuthorById() {
        var expectedAuthor = new Author(2, "Author_2");
        var actualAuthor = repositoryJdbc.findById(2);
        assertThat(actualAuthor)
                .isPresent()
                .get()
                .isEqualTo(expectedAuthor);
    }

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }

}