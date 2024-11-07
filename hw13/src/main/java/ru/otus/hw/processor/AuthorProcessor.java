package ru.otus.hw.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.dto.AuthorDTO;
import ru.otus.hw.models.mongo.AuthorMongo;
import org.springframework.lang.NonNull;

@Component
@RequiredArgsConstructor
public class AuthorProcessor implements ItemProcessor<AuthorMongo, AuthorDTO> {

    private final AuthorConverter authorConverter;

    @Override
    public AuthorDTO process(@NonNull AuthorMongo item) {
        return authorConverter.modelToDTO(item);
    }

}