package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDTO;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final CommentConverter commentConverter;

    @Transactional(readOnly = true)
    @Override
    public Optional<CommentDTO> findById(String id) {
        var commentDTO = commentRepository.findById(id).map(commentConverter::modelToDTO).orElse(null);
        return Optional.ofNullable(commentDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDTO> findByBookId(String bookId) {
        return commentConverter.modelsToDTO(commentRepository.findByBookId(bookId));
    }

    @Transactional
    @Override
    public CommentDTO insert(String content, String bookId) {
        var book = getBookById(bookId);
        var comment =  new Comment(null, content, book);
        return commentConverter.modelToDTO(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public CommentDTO update(String id, String content, String bookId) {
        var book = getBookById(bookId);
        var comment = getCommentById(id);
        comment.setContent(content);
        comment.setBook(book);
        return commentConverter.modelToDTO(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public void deleteById(String id) {
        commentRepository.findById(id).ifPresent(commentRepository::delete);
    }

    private Comment getCommentById(String id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %s not found".formatted(id)));
    }

    private Book getBookById(String bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));
    }

}
