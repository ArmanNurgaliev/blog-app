package ru.arman.notificationservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentNotificationDto {
    private String postOwnerEmail;
    private String postOwnerName;
    private String commentOwnerName;
    private String content;
}
