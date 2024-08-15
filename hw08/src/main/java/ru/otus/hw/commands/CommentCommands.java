package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentToStringConverter;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

    private final CommentService commentService;

    private final CommentToStringConverter commentToStringConverter;

    @ShellMethod(value = "Find all comments by book id", key = "cbbid")
    public String findAllBooks(String bookId) {
        return commentService.findByBookId(bookId).stream()
                .map(commentToStringConverter::commentToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find comment by id", key = "cbid")
    public String findCommentById(String id) {
        return commentService.findById(id)
                .map(commentToStringConverter::commentToString)
                .orElse("Comment with id %s not found".formatted(id));
    }

    @ShellMethod(value = "Insert comment", key = "cins")
    public String insertComment(String content, String bookId) {
        var savedComment = commentService.insert(content, bookId);
        return commentToStringConverter.commentToString(savedComment);
    }

    @ShellMethod(value = "Update comment", key = "cupd")
    public String updateComment(String id, String content, String bookId) {
        var savedComment = commentService.update(id, content, bookId);
        return commentToStringConverter.commentToString(savedComment);
    }

    @ShellMethod(value = "Delete comment by id", key = "cdel")
    public void deleteBook(String id) {
        commentService.deleteById(id);
    }

}
