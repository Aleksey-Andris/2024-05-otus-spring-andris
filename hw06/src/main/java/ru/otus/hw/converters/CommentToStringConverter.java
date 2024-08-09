package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDTO;

@RequiredArgsConstructor
@Component
public class CommentToStringConverter {

    public String commentToString(CommentDTO comment) {
         return "Id: %d, title: %s".formatted(comment.getId(), comment.getContent());
    }

}
