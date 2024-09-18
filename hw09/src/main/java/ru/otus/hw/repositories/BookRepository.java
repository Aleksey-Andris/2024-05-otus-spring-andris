package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository  extends JpaRepository<Book, Long> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "author-entity-graph")
    Optional<Book> findById(long id);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "author-entity-graph")
    @Query("SELECT b FROM Book b ORDER BY b.author.fullName, b.title")
    List<Book> findAllByOrderByAuthorFullNameTitle();

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "author-entity-graph")
    List<Book> findAllByAuthorIdOrderByTitle(long authorId);

}
