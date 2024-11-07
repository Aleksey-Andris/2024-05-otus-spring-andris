package ru.otus.hw.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDTO;
import ru.otus.hw.models.mongo.CommentMongo;

@Component
@RequiredArgsConstructor
public class CommentProcessor implements ItemProcessor<CommentMongo, CommentDTO> {

    private final CommentConverter commentConverter;

    @Override
    public CommentDTO process(@NonNull CommentMongo item)  {
        return commentConverter.modelToDTO(item);
    }

}
