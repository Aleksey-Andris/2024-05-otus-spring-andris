package ru.otus.hw.repositories.jpa;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.jpa.CommentJpa;

import java.util.List;

public interface CommentRepositoryJpa extends JpaRepository<CommentJpa, Long> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "book-entity-graph")
    List<CommentJpa> findAll();

}
