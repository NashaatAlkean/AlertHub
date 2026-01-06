package com.alerthub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SmsMessage {
    @NotBlank(message = "Phone number is required")
    private String to;
    @NotBlank(message = "Message content cannot be empty")
    private String message;
    private String actionId;

}
