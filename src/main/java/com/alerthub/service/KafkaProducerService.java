package com.alerthub.service;

import com.alerthub.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    @Value("${kafka.topic.email}")
    private String emailTopic;

    public void sendEmailNotification(NotificationMessage message) {
        log.info("Sending email notification to topic: {}", emailTopic);

        CompletableFuture<SendResult<String, NotificationMessage>> future =
                kafkaTemplate.send(emailTopic, message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message sent successfully to topic: {} with offset: {}",
                        emailTopic, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send message to topic: {}", emailTopic, ex);
            }
        });
    }
}