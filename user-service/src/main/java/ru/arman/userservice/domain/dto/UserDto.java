package ru.arman.userservice.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


public record UserDto(
        @NotEmpty(message = "Login can't be empty")
        String login,
        @NotEmpty(message = "Password can't be empty")
        String password,
        @Email(message = "Email address not valid")
        String email,
        String firstName,
        String middleName,
        String lastName,
        @NotEmpty(message = "Mobile phone can't be empty")
        String mobile,
        String intro,
        String profile) {
}
