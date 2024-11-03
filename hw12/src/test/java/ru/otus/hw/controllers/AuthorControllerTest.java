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
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.dto.AuthorDTO;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@DisplayName("Контроллер по операциям с авторами")
@WebMvcTest({AuthorController.class, AuthorConverter.class, ExceptionHandlerController.class})
@Import({SecurityConfig.class, SecurityPropertyProviderImpl.class})
class AuthorControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private UserDetailsService userDetailsServiceImpl;

    private List<AuthorDTO> authors;

    @BeforeEach
    void setUp() {
        var author1 = new AuthorDTO(1, "Author_1");
        var author2 = new AuthorDTO(2, "Author_2");
        var author3 = new AuthorDTO(3, "Author_3");
        authors = List.of(author1, author2, author3);
    }

    @WithMockUser(username = "user", authorities = "ROLE_USER")
    @Test
    @DisplayName("Должен загружать страницу просмотра всех авторов")
    void shoutBeLoadReadAllPage() throws Exception {
        when(authorService.findAll()).thenReturn(authors);

        mvc.perform(get("/author"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("authors", authors))
                .andExpect(view().name("authorList"));
    }

    @Test
    @DisplayName("Должен перекидывать на страницу  входа")
    void shoutBeRedirectToLoginPage() throws Exception {
        mvc.perform(get("/author"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

}