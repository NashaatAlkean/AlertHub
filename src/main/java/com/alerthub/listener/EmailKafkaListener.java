package com.alerthub.listener;

import com.alerthub.dto.EmailRequest;
import com.alerthub.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EmailKafkaListener {

    private static final Logger logger = LoggerFactory.getLogger(EmailKafkaListener.class);
    private final ObjectMapper objectMapper = new ObjectMapper(); // הכלי שממיר JSON לאובייקט

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "email", groupId = "email-group")
    public void listen(String messageJson) {
        try {
            // המרה ידנית מטקסט לאובייקט EmailRequest
            EmailRequest emailRequest = objectMapper.readValue(messageJson, EmailRequest.class);

            // שליחה ל-Service שביצענו קודם
            emailService.sendEmail(emailRequest);

        } catch (Exception e) {
            // דרישת לוגים מהסילבוס - רישום כישלון בפענוח ההודעה
            logger.error("Failed to decode Email message: {}", e.getMessage());
        }
    }
}