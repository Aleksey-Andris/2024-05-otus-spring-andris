package ru.otus.hw.mongo.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@ChangeLog
public class DatabaseChangelog {

    @ChangeSet(order = "001", id = "initData", author = "andris")
    public void initData(AuthorRepository authorRepository, GenreRepository genreRepository,
                              BookRepository bookRepository, CommentRepository commentRepository) {

        var author1 = authorRepository.save(new Author("1", "Author_1"));
        var author2 = authorRepository.save(new Author("2", "Author_2"));
        var author3 = authorRepository.save(new Author("3", "Author_3"));

        var genre1 = genreRepository.save(new Genre("1", "Genre_1"));
        var genre2 = genreRepository.save(new Genre("2", "Genre_2"));
        var genre3 = genreRepository.save(new Genre("3", "Genre_3"));
        var genre4 = genreRepository.save(new Genre("4", "Genre_4"));
        var genre5 = genreRepository.save(new Genre("5", "Genre_5"));
        var genre6 = genreRepository.save(new Genre("6", "Genre_6"));

        var book1 = bookRepository.save(new Book("1", "BookTitle_1", author1, List.of(genre1, genre2)));
        var book2 = bookRepository.save(new Book("2", "BookTitle_2", author2, List.of(genre3, genre4)));
        var book3 = bookRepository.save(new Book("3", "BookTitle_3", author3, List.of(genre5, genre6)));

        commentRepository.save(new Comment("1", "Comment_1", book1));
        commentRepository.save(new Comment("2", "Comment_2", book2));
        commentRepository.save(new Comment("3", "Comment_3", book3));
    }

}
