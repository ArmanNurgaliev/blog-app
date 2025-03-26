package ru.arman.commentservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.arman.commentservice.domain.dto.CommentDto;
import ru.arman.commentservice.domain.mapper.CommentMapper;
import ru.arman.commentservice.domain.model.Comment;
import ru.arman.commentservice.exception.CommentCreationException;
import ru.arman.commentservice.exception.CommentNotFoundException;
import ru.arman.commentservice.kafka.KafkaProducer;
import ru.arman.commentservice.repository.CommentRepository;
import ru.arman.commentservice.service.CommentService;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final KafkaProducer kafkaProducer;

//    @Override
//    public CommentDto addComment(Principal principal, Long postId, String content) {
//        log.info("Adding new Comment for post with id: {}", postId);
//        Comment comment = createComment((JwtAuthenticationToken) principal, postId, content);
//
//        log.info("Saving new comment");
//        Comment savedComment = commentRepository.save(comment);
//
//        kafkaProducer.sendNotification((JwtAuthenticationToken) principal, postId, content);
//
//        return commentMapper.commentToCommentDto(savedComment);
//    }

    public CompletableFuture<CommentDto> addComment(Principal principal, Long postId, String content) {
        log.info("Adding new Comment for post with id: {}", postId);

        return CompletableFuture.supplyAsync(() -> {
                    Comment comment = createComment((JwtAuthenticationToken) principal, postId, content);
                    log.info("Saving new comment");
                    return commentRepository.save(comment);
                })
                .thenApplyAsync(commentMapper::commentToCommentDto)
                .thenApplyAsync(savedComment -> {
                    log.info("Sending notification to Kafka");
                    kafkaProducer.sendNotification((JwtAuthenticationToken) principal, postId, content);
                    return savedComment;
                })
                .exceptionally(ex -> {
                    log.error("Error occurred while adding comment: {}", ex.getMessage());
                    throw new CommentCreationException(ex.getMessage());
                });
    }

    @Override
    public List<CommentDto> getPostComments(Long postId) {
        log.info("Searching for comments from post with id: {}", postId);
        return commentRepository.findCommentsByPostIdQuery(postId)
                .stream()
                .map(commentMapper::commentToCommentDto)
                .toList();
    }

    @Override
    public CommentDto addReply(Principal principal, Long postId, Long commentId, String content) {
        log.info("Adding reply to comment with id: {}", commentId);
        Comment comment = createComment((JwtAuthenticationToken) principal, postId, content);

        log.info("Searching for comment  with id: {}", commentId);
        Comment parentComment = getCommentById(commentId);
        comment.setParentComment(parentComment);

        return commentMapper.commentToCommentDto(commentRepository.save(comment));
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));
    }

    @Override
    public String updateComment(Long commentId, String content) {
        Comment comment = getCommentById(commentId);
        if (content != null && !content.isBlank())
            comment.setContent(content);
        commentRepository.save(comment);

        return "Comment was updated";
    }

    @Override
    public String deleteComment(Long commentId) {
        log.info("Deleting comment with id: {}", commentId);
        commentRepository.deleteById(commentId);

        return "Comment with id: " + commentId + " was deleted";
    }

    private Comment createComment(JwtAuthenticationToken principal, Long postId, String content) {
        Comment comment = new Comment();
        comment.setPostId(postId);

        log.info("Extract userId from token");
        String userId = (String) principal.getTokenAttributes().get("sub");
        comment.setUserId(userId);

        comment.setContent(content);
        comment.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

        return comment;
    }

    public boolean isUserOwner(Principal principal, Long commentId) {
        return commentRepository.findById(commentId)
                .map(comment -> comment.getUserId().equals(principal.getName()))
                .orElse(false);
    }
}
