package ru.otus.hw.converters;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.otus.hw.dto.AuthorDTO;
import ru.otus.hw.models.Author;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthorConverter {

    AuthorDTO modelToDTO(Author model);

    List<AuthorDTO> modelsToDTO(List<Author> models);

}
