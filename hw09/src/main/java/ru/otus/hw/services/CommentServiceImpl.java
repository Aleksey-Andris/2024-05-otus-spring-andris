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
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final CommentConverter commentConverter;

    @Transactional(readOnly = true)
    @Override
    public Optional<CommentDTO> findById(long id) {
        var commentDTO = commentRepository.findById(id).map(commentConverter::modelToDTO).orElse(null);
        return Optional.ofNullable(commentDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDTO> findByBookId(long bookId) {
        return commentConverter.modelsToDTO(commentRepository.findByBookId(bookId));
    }

    @Transactional
    @Override
    public CommentDTO insert(CommentDTO commentDTO, long bookId) {
        var book = getBookById(bookId);
        var comment = new Comment(0, commentDTO.getContent(), book);
        return commentConverter.modelToDTO(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        commentRepository.findById(id).ifPresent(commentRepository::delete);
    }

    @Override
    public List<CommentDTO> update(List<CommentDTO> commentsDTO) {
        var comments = getCommentsByDTO(commentsDTO);
        var commentsDtoMap = commentsDTO.stream().collect(Collectors.toMap(CommentDTO::getId, commentDTO -> commentDTO));
        comments.forEach(comment -> commentConverter.updateModelFromDTO(comment, commentsDtoMap.get(comment.getId())));
        return commentConverter.modelsToDTO(commentRepository.saveAll(comments));
    }

    private List<Comment> getCommentsByDTO(List<CommentDTO> commentsDTO) {
        Set<Long> commentsIds = commentsDTO.stream().map(CommentDTO::getId).collect(Collectors.toSet());
        var comments = commentRepository.findByIdIn(commentsIds);
        if (isEmpty(comments) || comments.size() != commentsDTO.size()) {
            throw new EntityNotFoundException("One or all comments with ids %s not found".formatted(commentsIds));
        }
        return comments;
    }

    private Book getBookById(long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
    }

}
