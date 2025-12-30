package com.Processor.dto;

import com.contracts.enums.ActionType;
import com.contracts.enums.RunOnDay;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * INBOUND DTO: Processor consumes this from the Action Queue (Kafka).
 * condition is an OR-of-AND matrix:
 *   outer list  = OR groups
 *   inner list  = AND metricIds
 * Example: [[m1, m2], [m3]] => (m1 AND m2) OR (m3)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionDTO {

    private UUID actionId;

    private ActionType actionType;      // SMS or EMAIL
    private String to;                  // email or phone
    private String message;             // notification body

    private LocalTime runOnTime;        // e.g., 08:00, 08:30
    private RunOnDay runOnDay;          // ALL, MONDAY, ...

    private List<List<UUID>> condition; // OR-of-AND metric IDs
}
