package ru.arman.postservice.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.arman.postservice.domain.dto.PostDto;
import ru.arman.postservice.domain.dto.PostDtoResponse;
import ru.arman.postservice.domain.model.Post;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    PostDtoResponse postToPostDtoResponse(Post post);
    Post postDtoToPost(PostDto postDto);
}
