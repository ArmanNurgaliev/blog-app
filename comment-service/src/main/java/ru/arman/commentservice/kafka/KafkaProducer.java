package ru.arman.commentservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.arman.commentservice.domain.dto.CommentNotificationDto;
import ru.arman.commentservice.domain.dto.UsersData;
import ru.arman.commentservice.feign.PostFeignClient;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PostFeignClient postFeignClient;
    @Value("${spring.kafka.topic-name}")
    private String topicName;

    public void sendNotification(JwtAuthenticationToken principal, Long postId, String content) {
        String login = (String) principal.getTokenAttributes().get("preferred_username");

        UsersData usersNameAndEmail = postFeignClient.getUsersNameAndEmail(postId);

        CommentNotificationDto commentNotificationDto = new CommentNotificationDto(usersNameAndEmail.email(), usersNameAndEmail.name(), login, content);

        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicName, commentNotificationDto);;
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent message=[{} with offset=[{}]", commentNotificationDto, result.getRecordMetadata().offset());
                } else {
                    log.error("Unable to send message=[{}] due to: {}", commentNotificationDto, ex.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage());
        }
    }
}
