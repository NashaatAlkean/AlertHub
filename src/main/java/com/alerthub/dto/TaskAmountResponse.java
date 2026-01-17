package com.alerthub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskAmountResponse {

    private String developerId;
    private Long taskCount;
    private Integer sinceDays;
}
