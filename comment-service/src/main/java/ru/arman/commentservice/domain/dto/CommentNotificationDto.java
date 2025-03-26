package ru.arman.commentservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentNotificationDto {
    private String postOwnerEmail;
    private String postOwnerName;
    private String commentOwnerName;
    private String content;
}
