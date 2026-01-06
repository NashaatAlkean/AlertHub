package com.alerthub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {

    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format") // בודק אוטומטית שיש שטרודל ונקודה
    private String to;

    @NotBlank(message = "Message content cannot be empty")
    private String message;

    // גטרים וסטרים...
}