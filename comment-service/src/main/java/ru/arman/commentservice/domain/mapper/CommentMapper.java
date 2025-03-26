package ru.arman.commentservice.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.arman.commentservice.domain.dto.CommentDto;
import ru.arman.commentservice.domain.model.Comment;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    @Mapping(target = "parent_comment_id", expression = "java(getParentId(comment))")
    CommentDto commentToCommentDto(Comment comment);

    default Long getParentId(Comment comment) {
        return comment.getParentComment() != null ?
                comment.getParentComment().getId() : 0;
    }
}
