package ru.otus.hw.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.GenreDTO;
import ru.otus.hw.models.mongo.GenreMongo;

@Component
@RequiredArgsConstructor
public class GenreProcessor implements ItemProcessor<GenreMongo, GenreDTO> {

    private final GenreConverter genreConverter;

    @Override
    public GenreDTO process(@NonNull GenreMongo item)  {
        return genreConverter.modelToDTO(item);
    }

}
