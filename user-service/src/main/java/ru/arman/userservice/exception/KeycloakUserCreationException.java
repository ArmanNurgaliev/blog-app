package ru.arman.userservice.exception;

public class KeycloakUserCreationException extends RuntimeException{
    public KeycloakUserCreationException(String message) {
        super(message);
    }
}
