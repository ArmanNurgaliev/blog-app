package ru.arman.notificationservice.service;

import ru.arman.notificationservice.domain.dto.CommentNotificationDto;

public interface EmailService {
    void sendEmail(CommentNotificationDto commentNotificationDto);
}
