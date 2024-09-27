package ru.otus.hw.controllers;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.otus.hw.dto.AuthorDTO;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Контроллер по операциям с авторами")
@WebMvcTest({AuthorRestController.class, AutoCloseable.class})
class AuthorRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthorService authorService;

    private List<AuthorDTO> authors;

    @BeforeEach
    void setUp() {
        var author1 = new AuthorDTO(1, "Author_1");
        var author2 = new AuthorDTO(2, "Author_2");
        var author3 = new AuthorDTO(3, "Author_3");
        authors = List.of(author1, author2, author3);
    }

    @Test
    @DisplayName("Должен получать список авторов")
    void shouldGetAuthors() throws Exception {
        when(authorService.findAll()).thenReturn(authors);

        MvcResult result = mvc.perform(get("/author"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<AuthorDTO> returnedAuthors = objectMapper.readValue(json, new TypeReference<>() {
        });
        assertThat(returnedAuthors)
                .isNotEmpty()
                .containsExactlyElementsOf(this.authors);
    }

}