package ru.otus.hw.converters;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.otus.hw.dto.CommentDTO;
import ru.otus.hw.models.Comment;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentConverter {

    CommentDTO modelToDTO(Comment model);

    List<CommentDTO> modelsToDTO(List<Comment> models);

}
