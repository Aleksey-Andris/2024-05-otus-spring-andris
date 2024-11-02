package ru.otus.hw.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookDTO;
import ru.otus.hw.models.mongo.BookMongo;

@Component
@RequiredArgsConstructor
public class BookProcessor implements ItemProcessor<BookMongo, BookDTO> {

    private final BookConverter bookConverter;

    @Override
    public BookDTO process(@NonNull BookMongo item) {
        return bookConverter.modelToDTO(item);
    }

}