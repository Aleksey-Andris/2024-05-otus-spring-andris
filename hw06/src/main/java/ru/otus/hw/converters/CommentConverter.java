package ru.otus.hw.converters;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.otus.hw.dto.CommentDTO;
import ru.otus.hw.models.Comment;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentConverter {

    CommentDTO modelToDTO(Comment model);

    List<CommentDTO> modelsToDTO(List<Comment> models);

}
