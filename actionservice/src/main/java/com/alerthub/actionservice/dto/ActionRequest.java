package com.alerthub.actionservice.dto;


import com.alerthub.actionservice.enums.ActionType;
import com.alerthub.actionservice.enums.RunOnDay;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * DTO for creating a new action
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionRequest {

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Condition is required")
    private String condition; // JSON format: [[1, 2], [3]]

    @NotBlank(message = "Recipient is required")
    private String recipient; // Phone or email

    @NotNull(message = "Action type is required")
    private ActionType actionType;

    @NotNull(message = "Run time is required")
    private LocalTime runOnTime;

    @NotBlank(message = "Run day is required")
    @Pattern(regexp = "Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday|All",
            message = "Run day must be a valid day of week or 'All'")
    private RunOnDay runonday;

    @NotBlank(message = "Message is required")
    @Size(max = 500, message = "Message must not exceed 500 characters")
    private String message;
}