package ru.otus.hw.repositories;


import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

    private final EntityManager entityManager;

    @Override
    public Optional<Book> findById(long id) {
        return Optional.ofNullable(entityManager.find(Book.class, id, getAuthorHint()));
    }

    @Override
    public List<Book> findAll() {
        var query = entityManager.createQuery("SELECT b FROM Book b", Book.class);
        setAuthorEntityGraph(query);
        return query.getResultList();
    }

    @Override
    public Book save(Book book) {
        return entityManager.merge(book);
    }

    @Override
    public void delete(Book book) {
        entityManager.remove(book);
    }

    private void setAuthorEntityGraph(TypedQuery<Book> query) {
        query.setHint(Graphs.FETCH_GRAPH.getVal(), getAuthorEntityGraph());
    }

    private Map<String, Object> getAuthorHint() {
        return Collections.singletonMap(Graphs.FETCH_GRAPH.getVal(), getAuthorEntityGraph());
    }

    private EntityGraph<?> getAuthorEntityGraph() {
        return entityManager.getEntityGraph("author-entity-graph");
    }

}
