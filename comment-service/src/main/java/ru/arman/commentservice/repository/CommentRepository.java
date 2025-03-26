package ru.arman.commentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.arman.commentservice.domain.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.replies WHERE c.id = :id")
    List<Comment> findCommentsByIdQuery(Long id);

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.replies WHERE c.postId = :id")
    List<Comment> findCommentsByPostIdQuery(Long id);
}
