package com.alerthub.actionservice.dto;

import com.alerthub.actionservice.enums.ActionType;
import com.alerthub.actionservice.enums.RunOnDay;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * DTO for returning Action data to clients.
 * Read-only representation of an Action.
 */
@Data
@Builder
public class ActionResponse {

    /**
     * Unique identifier of the action
     */
    private UUID id;

    /**
     * Owner of the action
     */
    private Integer userId;

    /**
     * Name of the action
     */
    private String name;

    /**
     * Condition matrix in JSON format
     */
    private String condition;

    /**
     * Notification type (EMAIL / SMS)
     */
    private ActionType actionType;

    /**
     * Notification destination (email or phone)
     */
    private String recipient;

    /**
     * Message content
     */
    private String message;

    /**
     * Scheduled run time
     */
    private LocalTime runOnTime;

    /**
     * Scheduled run day
     */
    private RunOnDay runonday;

    /**
     * Whether the action is enabled
     */
    private boolean enabled;

    /**
     * Creation timestamp
     */
    private LocalDateTime createDate;

    /**
     * Last update timestamp
     */
    private LocalDateTime lastUpdate;
}
