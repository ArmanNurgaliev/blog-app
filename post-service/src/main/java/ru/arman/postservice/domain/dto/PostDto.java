package ru.arman.postservice.domain.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record PostDto(
        @NotBlank(message = "Title can't be empty")
        String title,
        @NotBlank(message = "Content can't be empty")
        String content,
        List<Long> tagIds) {
}
