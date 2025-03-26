package ru.arman.postservice.domain.dto;

import lombok.Builder;
import ru.arman.postservice.domain.model.Tag;

import java.sql.Timestamp;
import java.util.Set;

@Builder
public record PostDtoResponse(
        long id,
        String userId,
        String title,
        String content,
        Timestamp createdAt,
        Timestamp updatedAt,
        Set<Tag> tags
) {
}
