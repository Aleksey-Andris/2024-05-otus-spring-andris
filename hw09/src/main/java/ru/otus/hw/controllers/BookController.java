package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
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
import ru.otus.hw.dto.AuthorDTO;
import ru.otus.hw.dto.BookDTO;
import ru.otus.hw.dto.BookShortDTO;
import ru.otus.hw.dto.GenreDTO;
import ru.otus.hw.exceptions.ContentNotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final GenreService genreService;

    private final AuthorService authorService;

    @GetMapping("book/{id}")
    public String getById(@PathVariable long id, Model model) {
        BookShortDTO book = bookService.findById(id).orElseThrow(ContentNotFoundException::new);
        List<AuthorDTO> authors = authorService.findAllOrderByFullName();
        model.addAttribute("book", book);
        model.addAttribute("authors", authors);
        List<GenreDTO> allGenres = genreService.findAll();
        List<GenreDTO> bookGenres = allGenres.stream().filter(genre ->
                book.getGenresIds().contains(genre.getId())).toList();
        model.addAttribute("allGenres", allGenres);
        model.addAttribute("bookGenres", bookGenres);
        return "book";
    }

    @GetMapping(value = "book/new", params = "authorId")
    public String getNewForm(@RequestParam("authorId") long authorId, Model model) {
        List<AuthorDTO> authors = authorService.findAllOrderByFullName();
        List<GenreDTO> allGenres = genreService.findAll();
        model.addAttribute("authors", authors);
        model.addAttribute("allGenres", allGenres);
        BookShortDTO book = new BookShortDTO();
        book.setAuthorId(authorId);
        model.addAttribute("newBook", book);
        return "newBook";
    }

    @GetMapping("book")
    public String getAll(Model model) {
        List<BookDTO> books = bookService.findAll();
        model.addAttribute("authorTitleIsVisible", false);
        model.addAttribute("authorColumnIsVisible", true);
        model.addAttribute("showGoToAllBooks", false);
        model.addAttribute("books", books);
        return "bookList";
    }

    @GetMapping(value = "book", params = "authorId")
    public String getByAuthorId(@RequestParam("authorId") long authorId, Model model) {
        List<BookDTO> books = bookService.findAllByAuthorId(authorId);
        model.addAttribute("authorTitleIsVisible", true);
        model.addAttribute("authorColumnIsVisible", false);
        model.addAttribute("showGoToAllBooks", true);
        model.addAttribute("books", books);
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
        return "redirect:/author";
    }

}
