package ru.otus.hw.repositories.jpa;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.jpa.BookJpa;

import java.util.List;

public interface BookRepositoryJpa extends JpaRepository<BookJpa, Long> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "author-entity-graph")
    List<BookJpa> findAll();

}
