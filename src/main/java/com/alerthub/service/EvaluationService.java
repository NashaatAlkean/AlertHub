package com.alerthub.service;

import com.alerthub.dto.DeveloperMostLabelResponse;
import com.alerthub.dto.LabelAggregateResponse;
import com.alerthub.dto.TaskAmountResponse;

public interface EvaluationService {

    /**
     * Find the developer with the most occurrences of a specific label within a specific time frame.
     *
     * @param label The label to search for (e.g., "bug", "enhancement")
     * @param sinceDays The number of days to look back from now
     * @return DeveloperMostLabelResponse containing developer info and count
     */
    DeveloperMostLabelResponse findDeveloperWithMostLabel(String label, Integer sinceDays);

    /**
     * Get aggregation of each label for the specified developer within a specific time frame.
     *
     * @param developerId The ID of the developer
     * @param sinceDays The number of days to look back from now
     * @return LabelAggregateResponse containing label counts for the developer
     */
    LabelAggregateResponse getLabelAggregateByDeveloper(String developerId, Integer sinceDays);

    /**
     * Get the total number of tasks assigned to a specified developer within a specific time frame.
     *
     * @param developerId The ID of the developer
     * @param sinceDays The number of days to look back from now
     * @return TaskAmountResponse containing the task count
     */
    TaskAmountResponse getTaskAmountByDeveloper(String developerId, Integer sinceDays);
}