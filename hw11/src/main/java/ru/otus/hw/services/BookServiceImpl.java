package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookDTO;
import ru.otus.hw.dto.BookShortDTO;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookConverter bookConverter;

    @Transactional(readOnly = true)
    @Override
    public Optional<BookDTO> findById(long id) {
        var bookDTO = bookRepository.findById(id).map(bookConverter::modelToDTO).orElse(null);
        return Optional.ofNullable(bookDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookDTO> findAll() {
        return bookConverter.modelsToDTO(bookRepository.findAllByOrderByAuthorFullNameTitle());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookDTO> findAllByAuthorId(long authorId) {
        return bookConverter.modelsToDTO(bookRepository.findAllByAuthorIdOrderByTitle(authorId));
    }

    @Transactional
    @Override
    public BookDTO insert(BookShortDTO bookShortDTO) {
        var author = getAuthorById(bookShortDTO.getAuthorId());
        var genres = getGenresByIds(bookShortDTO.getGenresIds());
        var book = new Book(0, bookShortDTO.getTitle(), author, genres);
        return bookConverter.modelToDTO(bookRepository.save(book));
    }

    @Transactional
    @Override
    public BookDTO update(BookShortDTO bookDTO) {
        var book = getBookById(bookDTO.getId());
        var author = getAuthorById(bookDTO.getAuthorId());
        var genres = getGenresByIds(bookDTO.getGenresIds());
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(author);
        book.setGenres(genres);
        return bookConverter.modelToDTO(bookRepository.save(book));
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        bookRepository.findById(id).ifPresent(bookRepository::delete);
    }

    private Book getBookById(long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
    }

    private Author getAuthorById(long authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
    }

    private List<Genre> getGenresByIds(Set<Long> genresIds) {
        var genres = genreRepository.findByIdIn(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }
        return genres;
    }

}
