package ru.otus.hw.converters;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.otus.hw.dto.GenreDTO;
import ru.otus.hw.models.mongo.GenreMongo;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GenreConverter {
    
    GenreDTO modelToDTO(GenreMongo model);
    
}
