package ru.otus.hw.converters;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import ru.otus.hw.dto.BookDTO;
import ru.otus.hw.models.mongo.BookMongo;
import ru.otus.hw.models.mongo.GenreMongo;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookConverter {

    @Mappings({
            @Mapping(target = "authorId", source = "author.id"),
            @Mapping(target = "genresIds", expression = "java(genresToIds(model))")
    })
    BookDTO modelToDTO(BookMongo model);

    default List<String> genresToIds(BookMongo model) {
        return model.getGenres()
                .stream()
                .map(GenreMongo::getId)
                .collect(Collectors.toList());
    }

}
