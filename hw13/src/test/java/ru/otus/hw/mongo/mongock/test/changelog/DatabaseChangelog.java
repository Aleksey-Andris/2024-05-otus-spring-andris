package ru.otus.hw.mongo.mongock.test.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import ru.otus.hw.models.mongo.AuthorMongo;
import ru.otus.hw.models.mongo.BookMongo;
import ru.otus.hw.models.mongo.CommentMongo;
import ru.otus.hw.models.mongo.GenreMongo;
import ru.otus.hw.repositories.mongo.AuthorRepositoryMongo;
import ru.otus.hw.repositories.mongo.BookRepositoryMongo;
import ru.otus.hw.repositories.mongo.CommentRepositoryMongo;
import ru.otus.hw.repositories.mongo.GenreRepositoryMongo;

import java.util.List;

@ChangeLog
public class DatabaseChangelog {

    @ChangeSet(order = "001", id = "initData", author = "andris")
    public void initData(AuthorRepositoryMongo authorRepository, GenreRepositoryMongo genreRepository,
                         BookRepositoryMongo bookRepository, CommentRepositoryMongo commentRepository) {

        AuthorMongo author1 = authorRepository.save(new AuthorMongo("1", "Author_1"));
        AuthorMongo author2 = authorRepository.save(new AuthorMongo("2", "Author_2"));
        AuthorMongo author3 = authorRepository.save(new AuthorMongo("3", "Author_3"));

        GenreMongo genre1 = genreRepository.save(new GenreMongo("1", "Genre_1"));
        GenreMongo genre2 = genreRepository.save(new GenreMongo("2", "Genre_2"));
        GenreMongo genre3 = genreRepository.save(new GenreMongo("3", "Genre_3"));
        GenreMongo genre4 = genreRepository.save(new GenreMongo("4", "Genre_4"));
        GenreMongo genre5 = genreRepository.save(new GenreMongo("5", "Genre_5"));
        GenreMongo genre6 = genreRepository.save(new GenreMongo("6", "Genre_6"));

        BookMongo book1 = bookRepository.save(new BookMongo("1", "BookTitle_1", author1, List.of(genre1, genre2)));
        BookMongo book2 = bookRepository.save(new BookMongo("2", "BookTitle_2", author2, List.of(genre3, genre4)));
        BookMongo book3 = bookRepository.save(new BookMongo("3", "BookTitle_3", author3, List.of(genre5, genre6)));

        commentRepository.save(new CommentMongo("1", "Comment_1", book1));
        commentRepository.save(new CommentMongo("2", "Comment_2", book2));
        commentRepository.save(new CommentMongo("3", "Comment_3", book3));
    }

}
