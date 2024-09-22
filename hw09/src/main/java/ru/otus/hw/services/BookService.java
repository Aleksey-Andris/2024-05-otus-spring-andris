package ru.otus.hw.services;

import ru.otus.hw.dto.BookDTO;
import ru.otus.hw.dto.BookShortDTO;

import java.util.List;
import java.util.Optional;

public interface BookService {

    Optional<BookDTO> findById(long id);

    List<BookDTO> findAll();

    List<BookDTO> findAllByAuthorId(long authorId);

    BookDTO insert(BookShortDTO bookShortDTO);

    BookDTO update(BookShortDTO book);

    void deleteById(long id);

}