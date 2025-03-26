package ru.arman.commentservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.arman.commentservice.domain.dto.CommentDto;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequestMapping("/api/v1/comment-service/comments")
public interface CommentController {

    @PostMapping("/add/{postId}")
    CompletableFuture<ResponseEntity<CommentDto>> addComment(Principal principal, @PathVariable Long postId, @RequestBody String content);

    @PostMapping("/add/{postId}/reply/{comment_id}")
    ResponseEntity<CommentDto> addReply(Principal principal, @PathVariable Long postId, @PathVariable Long comment_id, @RequestBody String content);

    @GetMapping("/post/{postId}")
    ResponseEntity<List<CommentDto>> getComments(@PathVariable Long postId);

    @PutMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @commentServiceImpl.isUserOwner(#principal, #commentId)")
    ResponseEntity<String> updateComment(@PathVariable Long commentId, @RequestBody String content);

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @commentServiceImpl.isUserOwner(#principal, #commentId)")
    ResponseEntity<String> deleteComment(@PathVariable Long commentId);
}
