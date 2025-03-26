package ru.arman.notificationservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.arman.notificationservice.domain.dto.CommentNotificationDto;
import ru.arman.notificationservice.service.EmailService;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String workingEmail;

    @Override
    public void sendEmail(CommentNotificationDto notificationDto) {
        log.info("Creating message to send by email");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(workingEmail);
        message.setTo(notificationDto.getPostOwnerEmail());
        message.setSubject("New comment in your post");
        message.setText(String.format("Hi, %s, your post was commented by +%s: %s", notificationDto.getPostOwnerName(), notificationDto.getCommentOwnerName(), notificationDto.getContent()));
        try {
            log.info("Trying to send email. (Need to uncomment and set credentials in yml)");
//            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("Error occurred while sending email: {}", e.getMessage());
        }
    }
}
