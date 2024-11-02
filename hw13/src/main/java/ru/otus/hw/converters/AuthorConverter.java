package ru.otus.hw.converters;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.otus.hw.dto.AuthorDTO;
import ru.otus.hw.models.mongo.AuthorMongo;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthorConverter {

    AuthorDTO modelToDTO(AuthorMongo model);

}
