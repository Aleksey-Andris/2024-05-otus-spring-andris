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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.otus.hw.dto.CommentDTO;
import ru.otus.hw.dto.CommentsFormDTO;
import ru.otus.hw.services.CommentService;

import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping(value = "comment", params = "bookId")
    public String getByBokId(@PathParam("bookId") long bookId, Model model) {
        List<CommentDTO> comments = commentService.findByBookId(bookId);
        model.addAttribute("commentsForm", new CommentsFormDTO(comments));
        model.addAttribute("bookId", bookId);
        model.addAttribute("newComment", new CommentDTO());
        return "commentList";
    }

    @PostMapping(value = "comment", params = "bookId")
    public String save(@Valid @ModelAttribute("newComment") CommentDTO comment, BindingResult bindingResult, @PathParam("bookId") long bookId, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("commentsForm", new CommentsFormDTO(Collections.emptyList()));
            return "commentList";
        }


        // return response;

        // commentService.update(comment); //TODO
        return "redirect:/comment?bookId=%s".formatted(bookId);
    }

    @PostMapping(value = "comments", params = "bookId")
    public String saveAll(@Valid @ModelAttribute("commentsForm") CommentsFormDTO commentsFormDTO,
                          BindingResult bindingResult, @PathParam("bookId") long bookId, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("newComment", new CommentDTO());
            return "commentList";
        }
        // commentService.update(comment); //TODO
        return "redirect:/comment?bookId=%s".formatted(bookId);
    }

    @DeleteMapping(value = "comment/{id}", params = "bookId")
    public String delete(@PathVariable long id, @PathParam("bookId") long bookId, Model model) {
        commentService.deleteById(id);
        return "redirect:/comment?bookId=%s".formatted(bookId);
    }

}

/*

<form action="commentList.html" th:action="@{'/comment/'+${comment.id}(bookId=${bookId})}"
                          th:method="delete">
                        <button class="destroy" th:text="#{delete-button-caption}"
                                style="font-weight:bold;background:darkred;color:aliceblue">Delete
                        </button>
                    </form>

        const xhttp = new XMLHttpRequest();
        xhttp.open("PATCH", url, true);
        xhttp.setRequestHeader("Content-Type", "application/json; charset=UTF-8")
        const body = JSON.stringify({
            id: 1,
            content: "Fix my bugs"
        });
        xhttp.onload = function () {
            let responseURL = xhttp.responseURL;
            console.log("Redirecting to:", responseURL);
            window.location.replace(responseURL);
        };
        xhttp.send(body);
 */
