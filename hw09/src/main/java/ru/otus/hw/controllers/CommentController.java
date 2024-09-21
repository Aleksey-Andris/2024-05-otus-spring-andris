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
import ru.otus.hw.dto.CommentDTO;
import ru.otus.hw.services.CommentService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping(value = "comment", params = "bookId")
    public String getByBokId(@PathParam("bookId") long bookId, Model model) {
        List<CommentDTO> comments = commentService.findByBookId(bookId);
        model.addAttribute("comments", comments);
        model.addAttribute("bookId", bookId);
        return "commentList";
    }

    @PatchMapping(value = "comment", params = "bookId")
    public String update(@Valid @ModelAttribute("comment") CommentDTO comment, @PathParam("bookId") long bookId, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
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
