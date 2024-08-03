package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaCommentRepository implements CommentRepository {

    private final EntityManager entityManager;

    @Override
    public Optional<Comment> findById(long id) {
        return Optional.ofNullable(entityManager.find(Comment.class, id, getBookHint()));
    }

    @Override
    public List<Comment> findByBook(Book book) {
        var query = entityManager.createQuery("SELECT c FROM Comment c WHERE c.book = :book", Comment.class);
        setBookEntityGraph(query);
        query.setParameter("book", book);
        return query.getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        return entityManager.merge(comment);
    }

    @Override
    public void delete(Comment comment) {
        entityManager.remove(comment);
    }

    private void setBookEntityGraph(TypedQuery<Comment> query) {
        query.setHint(Graphs.FETCH_GRAPH.getVal(), getBookEntityGraph());
    }

    private Map<String, Object> getBookHint() {
        return Collections.singletonMap(Graphs.FETCH_GRAPH.getVal(), getBookEntityGraph());
    }

    private EntityGraph<?> getBookEntityGraph() {
        return entityManager.getEntityGraph("book-entity-graph");
    }

}
