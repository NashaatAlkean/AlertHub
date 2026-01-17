package com.alerthub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabelAggregateResponse {

    private String developerId;
    private Map<String, Long> labelCounts;
    private Integer sinceDays;
    private Long totalTasks;
}