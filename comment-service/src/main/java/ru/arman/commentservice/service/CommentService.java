package ru.arman.commentservice.service;

import ru.arman.commentservice.domain.dto.CommentDto;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CommentService {
    CompletableFuture<CommentDto> addComment(Principal principal, Long postId, String content);

    List<CommentDto> getPostComments(Long postId);

    CommentDto addReply(Principal principal, Long postId, Long commentId, String content);

    String updateComment(Long commentId, String content);

    String deleteComment(Long commentId);
}
