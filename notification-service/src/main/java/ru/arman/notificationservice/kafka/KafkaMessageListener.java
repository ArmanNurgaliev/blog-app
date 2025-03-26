package ru.arman.notificationservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.arman.notificationservice.domain.dto.CommentNotificationDto;
import ru.arman.notificationservice.service.EmailService;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageListener {
    private final EmailService emailService;

    @KafkaListener(topics = "${spring.kafka.topic-name}", groupId = "email-message")
    public void handleEmailNotification(CommentNotificationDto commentNotificationDto) {
        log.info("Consumer gets message from topic: {}", commentNotificationDto);
        emailService.sendEmail(commentNotificationDto);
    }
}
