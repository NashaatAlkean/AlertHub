package com.alerthub.listener;

import com.alerthub.dto.SmsMessage;
import com.alerthub.service.SmsSenderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SmsKafkaListener {

    private static final Logger logger = LoggerFactory.getLogger(SmsKafkaListener.class);

    // הכלי שיהפוך את ה-String מה-Config לאובייקט SmsMessage
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SmsSenderService smsSenderService;

    @KafkaListener(topics = "sms-topic", groupId = "sms-group")
    public void listen(String messageJson) {
        logger.info("New message from Kafka: {}", messageJson);

        try {
            // המרה ידנית מ-String ל-DTO
            SmsMessage smsMessage = objectMapper.readValue(messageJson, SmsMessage.class);

            // שליחה ל-Service שכבר בודק ולידציה
            smsSenderService.sendSms(smsMessage);

        } catch (Exception e) {
            // אם ה-JSON שבור או שה-Service זרק שגיאה - זה יתועד כאן
            logger.error("Error processing SMS from Kafka: {}", e.getMessage());
        }
    }
}