package ru.arman.commentservice.exception;

public class CommentCreationException extends RuntimeException {
    public CommentCreationException(String message) {
        super(message);
    }
}
