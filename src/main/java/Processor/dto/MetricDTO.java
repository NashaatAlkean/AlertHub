package com.Processor.dto;

import com.contracts.enums.LabelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Used by Processor while evaluating conditions.
 * Typically fetched from Metric service by ID.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricDTO {

    private UUID id;

    // Optional in Processor; keep only if you need it for authorization/logging.
    private Integer userId;

    // Optional; useful for logs/debug.
    private String name;

    private LabelType label;
    private Integer threshold;
    private Integer timeFrameHours;
}
