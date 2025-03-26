package ru.arman.commentservice.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.arman.commentservice.controller.CommentController;
import ru.arman.commentservice.domain.dto.CommentDto;
import ru.arman.commentservice.service.CommentService;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class CommentControllerImpl implements CommentController {
    private final CommentService commentService;

    @Override
    public CompletableFuture<ResponseEntity<CommentDto>> addComment(Principal principal, Long postId, String content) {
        return commentService.addComment(principal, postId, content)
                .thenApply(commentDto -> new ResponseEntity<>(commentDto, HttpStatus.CREATED));
    }

    @Override
    public ResponseEntity<CommentDto> addReply(Principal principal, Long postId, Long comment_id, String content) {
        return new ResponseEntity<>(commentService.addReply(principal, postId, comment_id, content), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<CommentDto>> getComments(Long postId) {
        return ResponseEntity.ok(commentService.getPostComments(postId));
    }

    @Override
    public ResponseEntity<String> updateComment(Long commentId, String content) {
        return ResponseEntity.ok(commentService.updateComment(commentId, content));
    }

    @Override
    public ResponseEntity<String> deleteComment(Long commentId) {
        return ResponseEntity.ok(commentService.deleteComment(commentId));
    }
}
