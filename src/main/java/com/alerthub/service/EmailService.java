package com.alerthub.service;
import com.alerthub.dto.EmailRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    // הגדרת לוגר SLF4J לצורך שליחת לוגים ל-MongoDB בהמשך [cite: 265, 276]
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    /**
     * פונקציה לעיבוד ושליחת מייל [cite: 227]
     */
    public void sendEmail(EmailRequest emailRequest) {
        try {
            // 1. בדיקת תקינות (Validation) כחלק מטיפול בחריגות [cite: 48]
            if (emailRequest.getTo() == null || !emailRequest.getTo().contains("@")) {
                throw new IllegalArgumentException("Invalid email address provided");
            }

            // 2. סימולציית שליחה (כאן יבוא קוד שליחה אמיתי בעתיד)
            System.out.println("--- ALERT HUB EMAIL SERVICE ---");
            System.out.println("TO: " + emailRequest.getTo());
            System.out.println("MESSAGE: " + emailRequest.getMessage());

            // 3. רישום לוג הצלחה - דרישה חובה עבור שירות ה-Logger [cite: 275]
            logger.info("Email sent successfully to {}", emailRequest.getTo());

        } catch (Exception e) {
            // 4. רישום לוג כישלון במקרה של שגיאה [cite: 275]
            logger.error("Failed to send email to {}. Error: {}", emailRequest.getTo(), e.getMessage());

            // ניתן להוסיף כאן לוגיקה נוספת לטיפול בשגיאות כפי שנדרש [cite: 48]
        }
    }
}