package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDTO;

@RequiredArgsConstructor
@Component
public class CommentToStringConverter {

    public String commentToString(CommentDTO comment) {
         return "Id: %s, content: %s".formatted(comment.getId(), comment.getContent());
    }

}
