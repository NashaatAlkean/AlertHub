package com.alerthub.service;

import com.alerthub.dto.SmsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SmsSenderService {

    private static final Logger logger = LoggerFactory.getLogger(SmsSenderService.class);

    /**
     * פונקציה לעיבוד ושליחת SMS.
     * שים לב: אין כאן try-catch כי ה-GlobalExceptionHandler מטפל בזה!
     */
    public void sendSms(SmsMessage smsMessage) {

        // 1. בדיקת תקינות (Validation)
        // אם התנאי מתקיים, נזרקת חריגה והמתודה נעצרת כאן.
        if (smsMessage.getTo() == null || smsMessage.getTo().isEmpty()) {
            logger.error("SMS Failed: No recipient phone number provided");
            throw new IllegalArgumentException("Phone number is required and cannot be empty.");
        }

        // 2. סימולציית שליחה (כאן יבוא קוד שליחה אמיתי בעתיד)
        System.out.println("--- ALERT HUB SMS SERVICE ---");
        System.out.println("TO: " + smsMessage.getTo());
        System.out.println("MESSAGE: " + smsMessage.getMessage());
        System.out.println("ACTION ID: " + smsMessage.getActionId());

        // 3. רישום לוג הצלחה
        // זהו שלב קריטי כדי ששירות ה-Logger יוכל לאסוף את המידע מאוחר יותר
        logger.info("SMS sent successfully to {} for Action ID: {}",
                smsMessage.getTo(), smsMessage.getActionId());
    }
}