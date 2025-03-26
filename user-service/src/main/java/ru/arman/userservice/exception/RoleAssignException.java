package ru.arman.userservice.exception;

public class RoleAssignException extends RuntimeException{
    public RoleAssignException(String message) {
        super(message);
    }
}
