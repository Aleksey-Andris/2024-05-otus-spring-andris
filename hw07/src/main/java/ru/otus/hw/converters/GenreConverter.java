package ru.otus.hw.converters;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.otus.hw.dto.GenreDTO;
import ru.otus.hw.models.Genre;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GenreConverter {
    
    GenreDTO modelToDTO(Genre model);
    
    List<GenreDTO> modelsToDTO(List<Genre> models);
    
}
