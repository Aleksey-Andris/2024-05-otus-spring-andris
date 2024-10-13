package ru.otus.hw.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.config.SecurityConfig;
import ru.otus.hw.config.SecurityPropertyProviderImpl;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.AuthorDTO;
import ru.otus.hw.dto.BookDTO;
import ru.otus.hw.dto.BookShortDTO;
import ru.otus.hw.dto.GenreDTO;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@DisplayName("Контроллер по операциям с книгами")
@WebMvcTest({BookController.class, BookConverter.class, ExceptionHandlerController.class})
@Import({SecurityConfig.class, SecurityPropertyProviderImpl.class})
class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private GenreService genreService;

    @MockBean
    private AuthorService authorService;

    @Autowired
    private BookConverter bookConverter;

    @MockBean
    private UserDetailsService userDetailsServiceImpl;

    private List<AuthorDTO> authors;

    private List<GenreDTO> genres;

    private List<BookDTO> books;

    private List<BookShortDTO> shortBooks;

    @BeforeEach
    void setUp() {
        var author1 = new AuthorDTO(1, "Author_1");
        var author2 = new AuthorDTO(2, "Author_2");
        var author3 = new AuthorDTO(3, "Author_3");

        var genre1 = new GenreDTO(1, "Genre_1");
        var genre2 = new GenreDTO(2, "Genre_2");
        var genre3 = new GenreDTO(3, "Genre_3");
        var genre4 = new GenreDTO(4, "Genre_4");
        var genre5 = new GenreDTO(5, "Genre_5");
        var genre6 = new GenreDTO(6, "Genre_6");

        var book1 = new BookDTO(1, "BookTitle_1", author1, List.of(genre1, genre2));
        var book2 = new BookDTO(2, "BookTitle_2", author2, List.of(genre3, genre4));
        var book3 = new BookDTO(3, "BookTitle_3", author3, List.of(genre5, genre6));

        var bookShort1 = bookConverter.fullToShort(book1);
        var bookShort2 = bookConverter.fullToShort(book2);
        var bookShort3 = bookConverter.fullToShort(book3);

        authors = List.of(author1, author2, author3);
        genres = List.of(genre1, genre2, genre3, genre4, genre5, genre6);
        books = List.of(book1, book2, book3);
        shortBooks = List.of(bookShort1, bookShort2, bookShort3);
    }

    @WithMockUser(username = "someUser", authorities = "ROLE_USER")
    @Test
    @DisplayName("Должен загружать страницу просмотра и редактирования книги")
    void shoutBeLoadReadEditPage() throws Exception {
        var book = books.get(0);
        var shortBook = shortBooks.get(0);

        when(bookService.findById(book.getId())).thenReturn(Optional.of(book));
        when(authorService.findAll()).thenReturn(authors);
        when(genreService.findAll()).thenReturn(genres);

        mvc.perform(get("/book/{id}", book.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("book", shortBook))
                .andExpect(model().attribute("authors", authors))
                .andExpect(model().attribute("allGenres", genres))
                .andExpect(model().attribute("bookGenres", book.getGenres()))
                .andExpect(view().name("book"));
    }

    @WithMockUser(username = "someUser", authorities = "ROLE_USER")
    @Test
    @DisplayName("Должен загружать страницу с ошибкой")
    void shoutBeLoadErrorPage() throws Exception {
        var nonExistentId = 100500L;
        when(bookService.findById(nonExistentId)).thenReturn(Optional.empty());

        mvc.perform(get("/book/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(view().name("err404"));
    }

    @WithMockUser(username = "someUser", authorities = "ROLE_MODERATOR")
    @Test
    @DisplayName("Должен загружать страницу создания книги с предустановленным автором")
    void shoutBeLoadCreatePageWithSetAuthor() throws Exception {
        var newBook = new BookShortDTO();
        newBook.setAuthorId(1);

        when(authorService.findAll()).thenReturn(authors);
        when(genreService.findAll()).thenReturn(genres);

        mvc.perform(get("/book/new").param("authorId", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("authors", authors))
                .andExpect(model().attribute("allGenres", genres))
                .andExpect(model().attribute("newBook", newBook))
                .andExpect(view().name("newBook"));
    }

    @WithMockUser(username = "someUser", authorities = "ROLE_MODERATOR")
    @Test
    @DisplayName("Должен загружать страницу создания книги")
    void shoutBeLoadCreatePage() throws Exception {
        when(authorService.findAll()).thenReturn(authors);
        when(genreService.findAll()).thenReturn(genres);

        mvc.perform(get("/book/new").param("authorId", "0"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("authors", authors))
                .andExpect(model().attribute("allGenres", genres))
                .andExpect(model().attribute("newBook", new BookShortDTO()))
                .andExpect(view().name("newBook"));
    }

    @WithMockUser(username = "someUser", authorities = "ROLE_USER")
    @Test
    @DisplayName("Должен загружать страницу просмотра всех книг")
    void shoutBeLoadReadAllPage() throws Exception {
        when(bookService.findAll()).thenReturn(books);

        mvc.perform(get("/book"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("books", books))
                .andExpect(model().attribute("authorTitleIsVisible", false))
                .andExpect(model().attribute("authorColumnIsVisible", true))
                .andExpect(model().attribute("showGoToAllBooks", false))
                .andExpect(view().name("bookList"));
    }

    @WithMockUser(username = "someUser", authorities = "ROLE_USER")
    @Test
    @DisplayName("Должен загружать страницу просмотра всех книг автора")
    void shoutBeLoadReadAllPageByAuthor() throws Exception {
        var author = authors.get(2);
        var books = this.books.stream().filter(book ->
                book.getAuthor().getId() == author.getId()).collect(Collectors.toList());

        when(bookService.findAllByAuthorId(author.getId())).thenReturn(books);

        mvc.perform(get("/book").param("authorId", String.valueOf(author.getId())))
                .andExpect(status().isOk())
                .andExpect(model().attribute("books", books))
                .andExpect(model().attribute("authorTitleIsVisible", true))
                .andExpect(model().attribute("authorColumnIsVisible", false))
                .andExpect(model().attribute("showGoToAllBooks", true))
                .andExpect(view().name("bookList"));
    }

    @WithMockUser(username = "someUser", authorities = "ROLE_MODERATOR")
    @Test
    @DisplayName("Должен создавать книгу")
    void shouldBeCreateBook() throws Exception {
        var book = new BookShortDTO(0, "New book title", 2, Set.of(1L, 3L));

        mvc.perform(post("/book").flashAttr("newBook", book))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book"));

        verify(bookService, times(1)).insert(book);
    }

    @WithMockUser(username = "someUser", authorities = "ROLE_MODERATOR")
    @Test
    @DisplayName("Должен изменять книгу")
    void shouldBeUpdateBook() throws Exception {
        var book = new BookShortDTO(1, "New book title", 2, Set.of(1L, 3L));

        mvc.perform(patch("/book").flashAttr("book", book))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book/1"));

        verify(bookService, times(1)).update(book);
    }

    @WithMockUser(username = "someUser", authorities = "ROLE_ADMIN")
    @Test
    @DisplayName("Должен удалять книгу")
    void shouldBeDeleteBook() throws Exception {
        mvc.perform(delete("/book/{id}", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/author"));

        verify(bookService, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Должен перекидывать на страницу  входа")
    void shoutBeRedirectToLoginPage() throws Exception {
        mvc.perform(get("/book/{id}", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        mvc.perform(get("/book/new").param("authorId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        mvc.perform(get("/book"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @WithMockUser(username = "someUser", authorities = {"ROLE_USER", "ROLE_ADMIN"})
    @Test
    @DisplayName("Должен возвращать ошибку при попытке изменить или сохранить данные")
    void shouldBeReturnedErrorWhenAttemptingToChangeOrSaveData() throws Exception {
        var book = books.get(0);
        var newBook = new BookShortDTO(1, "New book title", 2, Set.of(1L, 3L));

        mvc.perform(get("/book/new").param("authorId", "0"))
                .andExpect(status().isForbidden());

        mvc.perform(patch("/book").flashAttr("book", book))
                .andExpect(status().isForbidden());

        mvc.perform(post("/book").flashAttr("newBook", newBook))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "someUser", authorities = {"ROLE_USER", "ROLE_MODERATOR"})
    @Test
    @DisplayName("Должен возвращать ошибку при попытке удалить данные")
    void shouldBeReturnedErrorWhenAttemptingToDeleteData() throws Exception {
        mvc.perform(delete("/book/{id}", 1))
                .andExpect(status().isForbidden());
    }

}