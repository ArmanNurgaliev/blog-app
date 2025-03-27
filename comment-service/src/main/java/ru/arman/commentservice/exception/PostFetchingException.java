package ru.arman.commentservice.exception;

public class PostFetchingException extends RuntimeException {
    public PostFetchingException(String message) {
        super(message);
    }
}
