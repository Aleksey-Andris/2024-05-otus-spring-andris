package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookShortDTO;
import ru.otus.hw.exceptions.ContentNotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final GenreService genreService;

    private final AuthorService authorService;

    private final BookConverter bookConverter;

    @GetMapping("book/{id}")
    public String getById(@PathVariable long id, Model model) {
        var book = bookService.findById(id).orElseThrow(ContentNotFoundException::new);
        var shortBook = bookConverter.fullToShort(book);
        var authors = authorService.findAll();
        var allGenres = genreService.findAll();
        model.addAttribute("book", shortBook);
        model.addAttribute("authors", authors);
        model.addAttribute("allGenres", allGenres);
        model.addAttribute("bookGenres", book.getGenres());
        return "book";
    }

    @GetMapping(value = "book/new", params = "authorId")
    public String getNewForm(@RequestParam("authorId") long authorId, Model model) {
        var authors = authorService.findAll();
        var allGenres = genreService.findAll();
        var newBook = new BookShortDTO();
        newBook.setAuthorId(authorId);
        model.addAttribute("authors", authors);
        model.addAttribute("allGenres", allGenres);
        model.addAttribute("newBook", newBook);
        return "newBook";
    }

    @GetMapping("book")
    public String getAll(Model model) {
        var books = bookService.findAll();
        model.addAttribute("books", books);
        model.addAttribute("authorTitleIsVisible", false);
        model.addAttribute("authorColumnIsVisible", true);
        model.addAttribute("showGoToAllBooks", false);
        return "bookList";
    }

    @GetMapping(value = "book", params = "authorId")
    public String getByAuthorId(@RequestParam("authorId") long authorId, Model model) {
        var books = bookService.findAllByAuthorId(authorId);
        model.addAttribute("books", books);
        model.addAttribute("authorTitleIsVisible", true);
        model.addAttribute("authorColumnIsVisible", false);
        model.addAttribute("showGoToAllBooks", true);
        return "bookList";
    }

    @PostMapping("book")
    public String save(@Valid @ModelAttribute("newBook") BookShortDTO book, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "newBook";
        }
        bookService.insert(book);
        return "redirect:/book";
    }

    @PatchMapping("book")
    public String update(@Valid @ModelAttribute("book") BookShortDTO book, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "book";
        }
        bookService.update(book);
        return "redirect:/book/%s".formatted(book.getId());
    }

    @DeleteMapping("book/{id}")
    public String delete(@PathVariable long id, Model model) {
        bookService.deleteById(id);
        return "redirect:/authors-page";
    }

}
