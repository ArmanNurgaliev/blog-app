package ru.arman.userservice.domain.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserDtoResponse(
        UUID id,
        String login,
        String email,
        String mobile,
        String firstName,
        String middleName,
        String lastName,
        String intro,
        String profile
) {
}
