package ru.arman.commentservice.domain.dto;

import lombok.Builder;

import java.sql.Timestamp;
import java.util.List;

@Builder
public record CommentDto(
        long id,
        long postId,
        String userId,
        String content,
        long parent_comment_id,
        List<CommentDto> replies,
        Timestamp createdAt
) {
}
