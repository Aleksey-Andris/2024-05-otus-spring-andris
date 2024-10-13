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
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDTO;
import ru.otus.hw.dto.CommentsForm;
import ru.otus.hw.services.CommentService;

import java.util.List;

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

@DisplayName("Контроллер по операциям с комментариями")
@WebMvcTest({CommentController.class, CommentConverter.class, ExceptionHandlerController.class})
@Import({SecurityConfig.class, SecurityPropertyProviderImpl.class})
class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserDetailsService userDetailsServiceImpl;

    private List<CommentDTO> comments;

    private CommentsForm commentsForm;

    @BeforeEach
    void setUp() {
        var comment1 = new CommentDTO(1, "Comment_1");
        var comment2 = new CommentDTO(2, "Comment_2");
        var comment3 = new CommentDTO(3, "Comment_3");
        comments = List.of(comment1, comment2, comment3);
        commentsForm = new CommentsForm(comments);
    }

    @WithMockUser(username = "someUser", authorities = "ROLE_USER")
    @Test
    @DisplayName("Должен загружать страницу просмотра комментариев по книге")
    void shoutBeLoadReadPageByBook() throws Exception {
        when(commentService.findByBookId(1)).thenReturn(comments);

        mvc.perform(get("/comment").param("bookId", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("commentsForm", commentsForm))
                .andExpect(model().attribute("bookId", 1L))
                .andExpect(model().attribute("newComment", new CommentDTO()))
                .andExpect(view().name("commentList"));
    }

    @WithMockUser(username = "someUser", authorities = "ROLE_MODERATOR")
    @Test
    @DisplayName("Должен создавать комментарий")
    void shouldBeCreateComment() throws Exception {
        var comment = new CommentDTO(0, "Comment_new");

        mvc.perform(post("/comment").flashAttr("newComment", comment).param("bookId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/comment?bookId=1"));

        verify(commentService, times(1)).insert(comment, 1);
    }

    @WithMockUser(username = "someUser", authorities = "ROLE_MODERATOR")
    @Test
    @DisplayName("Должен изменять комментарии")
    void shouldBeChangeComments() throws Exception {
        mvc.perform(patch("/comments").flashAttr("commentsForm", commentsForm).param("bookId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/comment?bookId=1"));

        verify(commentService, times(1)).update(comments);
    }

    @WithMockUser(username = "someUser", authorities = "ROLE_ADMIN")
    @Test
    @DisplayName("Должен удалять комментарий")
    void shouldBeDeleteComment() throws Exception {
        mvc.perform(delete("/comment/{id}", 1).param("bookId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/comment?bookId=1"));

        verify(commentService, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Должен перекидывать на страницу  входа")
    void shoutBeRedirectToLoginPage() throws Exception {
        mvc.perform(get("/comment").param("bookId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @WithMockUser(username = "someUser", authorities = {"ROLE_USER", "ROLE_ADMIN"})
    @Test
    @DisplayName("Должен возвращать ошибку при попытке изменить или сохранить данные")
    void shouldBeReturnedErrorWhenAttemptingToChangeOrSaveData() throws Exception {
        var comment = new CommentDTO(0, "Comment_new");

        mvc.perform(post("/comment").flashAttr("newComment", comment).param("bookId", "1"))
                .andExpect(status().isForbidden());

        mvc.perform(patch("/comments").flashAttr("commentsForm", commentsForm).param("bookId", "1"))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "someUser", authorities = {"ROLE_USER", "ROLE_MODERATOR"})
    @Test
    @DisplayName("Должен возвращать ошибку при попытке удалить данные")
    void shouldBeReturnedErrorWhenAttemptingToDeleteData() throws Exception {
        mvc.perform(delete("/comment/{id}", 1).param("bookId", "1"))
                .andExpect(status().isForbidden());
    }

}