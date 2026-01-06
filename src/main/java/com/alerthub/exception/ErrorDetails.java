package com.alerthub.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {
    private Date timestamp;    // מתי קרתה השגיאה
    private String message;     // הודעה קצרה (למשל: "Invalid Email")
    private String details;     // פרטים נוספים (למשל: הנתיב שבו קרתה השגיאה)
}