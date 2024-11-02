package ru.otus.hw.converters;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import ru.otus.hw.dto.CommentDTO;
import ru.otus.hw.models.mongo.CommentMongo;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentConverter {

    @Mappings({
            @Mapping(target = "bookId", source = "book.id")
    })
    CommentDTO modelToDTO(CommentMongo model);

}
