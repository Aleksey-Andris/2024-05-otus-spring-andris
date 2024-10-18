package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByBookId(long bookId);

     List<Comment> findByIdIn(Set<Long> ids);

}
