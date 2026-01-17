package com.alerthub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeveloperMostLabelResponse {

    private String developerId;
    private String label;
    private Long count;
    private Integer sinceDays;
}