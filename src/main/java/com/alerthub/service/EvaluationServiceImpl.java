package com.alerthub.service;

import com.alerthub.dto.*;
import com.alerthub.enums.Label;
import com.alerthub.exception.InvalidParameterException;
import com.alerthub.exception.ResourceNotFoundException;
import com.alerthub.repository.PlatformInformationRepository;
import com.alerthub.service.EvaluationService;
import com.alerthub.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvaluationServiceImpl implements EvaluationService {

    private final PlatformInformationRepository platformInformationRepository;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public DeveloperMostLabelResponse findDeveloperWithMostLabel(String label, Integer sinceDays) {
        log.info("Finding developer with most occurrences of label: {} in last {} days", label, sinceDays);

        validateLabel(label);
        validateSinceDays(sinceDays);

        LocalDateTime since = LocalDateTime.now().minusDays(sinceDays);

        List<Object[]> results = platformInformationRepository.findDeveloperWithMostLabel(label, since);

        if (results.isEmpty()) {
            throw new ResourceNotFoundException(
                    String.format("No tasks found with label '%s' in the last %d days", label, sinceDays));
        }

        Object[] topResult = results.get(0);
        String developerId = (String) topResult[0];
        Long count = (Long) topResult[1];

        DeveloperMostLabelResponse response = DeveloperMostLabelResponse.builder()
                .developerId(developerId)
                .label(label)
                .count(count)
                .sinceDays(sinceDays)
                .build();

        // Send notification to email queue
        sendNotification(response);

        log.info("Found developer {} with {} occurrences of label {}", developerId, count, label);
        return response;
    }

    @Override
    public LabelAggregateResponse getLabelAggregateByDeveloper(String developerId, Integer sinceDays) {
        log.info("Getting label aggregate for developer: {} in last {} days", developerId, sinceDays);

        validateDeveloperId(developerId);
        validateSinceDays(sinceDays);

        LocalDateTime since = LocalDateTime.now().minusDays(sinceDays);

        List<Object[]> results = platformInformationRepository.findLabelAggregateByDeveloper(developerId, since);

        Map<String, Long> labelCounts = new HashMap<>();
        long totalTasks = 0;

        for (Object[] result : results) {
            String label = (String) result[0];
            Long count = (Long) result[1];
            labelCounts.put(label, count);
            totalTasks += count;
        }

        if (labelCounts.isEmpty()) {
            throw new ResourceNotFoundException(
                    String.format("No tasks found for developer '%s' in the last %d days", developerId, sinceDays));
        }

        LabelAggregateResponse response = LabelAggregateResponse.builder()
                .developerId(developerId)
                .labelCounts(labelCounts)
                .sinceDays(sinceDays)
                .totalTasks(totalTasks)
                .build();

        // Send notification to email queue
        sendNotification(response);

        log.info("Found {} different labels for developer {} with total {} tasks",
                labelCounts.size(), developerId, totalTasks);
        return response;
    }

    @Override
    public TaskAmountResponse getTaskAmountByDeveloper(String developerId, Integer sinceDays) {
        log.info("Getting task amount for developer: {} in last {} days", developerId, sinceDays);

        validateDeveloperId(developerId);
        validateSinceDays(sinceDays);

        LocalDateTime since = LocalDateTime.now().minusDays(sinceDays);

        Long taskCount = platformInformationRepository.countTasksByDeveloper(developerId, since);

        if (taskCount == null || taskCount == 0) {
            throw new ResourceNotFoundException(
                    String.format("No tasks found for developer '%s' in the last %d days", developerId, sinceDays));
        }

        TaskAmountResponse response = TaskAmountResponse.builder()
                .developerId(developerId)
                .taskCount(taskCount)
                .sinceDays(sinceDays)
                .build();

        // Send notification to email queue
        sendNotification(response);

        log.info("Found {} tasks for developer {} in last {} days", taskCount, developerId, sinceDays);
        return response;
    }

    private void validateLabel(String label) {
        if (label == null || label.trim().isEmpty()) {
            throw new InvalidParameterException("Label parameter cannot be null or empty");
        }

        try {
            Label.fromValue(label.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidParameterException(
                    String.format("Invalid label: '%s'. Valid labels are: bug, documentation, duplicate, " +
                            "enhancement, good_first_issue, help_wanted, invalid, question, wontfix", label));
        }
    }

    private void validateDeveloperId(String developerId) {
        if (developerId == null || developerId.trim().isEmpty()) {
            throw new InvalidParameterException("Developer ID cannot be null or empty");
        }
    }

    private void validateSinceDays(Integer sinceDays) {
        if (sinceDays == null || sinceDays <= 0) {
            throw new InvalidParameterException("Since days must be a positive integer");
        }
    }

    private void sendNotification(DeveloperMostLabelResponse response) {
        String message = String.format(
                "Evaluation Report - Developer with Most '%s' Labels\n\n" +
                        "Developer ID: %s\n" +
                        "Label: %s\n" +
                        "Count: %d\n" +
                        "Time Frame: Last %d days",
                response.getLabel(),
                response.getDeveloperId(),
                response.getLabel(),
                response.getCount(),
                response.getSinceDays()
        );

        NotificationMessage notification = NotificationMessage.builder()
                .subject("Evaluation Report - Most Label Occurrences")
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaProducerService.sendEmailNotification(notification);
    }

    private void sendNotification(LabelAggregateResponse response) {
        StringBuilder labelDetails = new StringBuilder();
        response.getLabelCounts().forEach((label, count) ->
                labelDetails.append(String.format("  - %s: %d\n", label, count)));

        String message = String.format(
                "Evaluation Report - Label Aggregate for Developer\n\n" +
                        "Developer ID: %s\n" +
                        "Time Frame: Last %d days\n" +
                        "Total Tasks: %d\n\n" +
                        "Label Breakdown:\n%s",
                response.getDeveloperId(),
                response.getSinceDays(),
                response.getTotalTasks(),
                labelDetails.toString()
        );

        NotificationMessage notification = NotificationMessage.builder()
                .subject("Evaluation Report - Label Aggregate")
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaProducerService.sendEmailNotification(notification);
    }

    private void sendNotification(TaskAmountResponse response) {
        String message = String.format(
                "Evaluation Report - Task Amount for Developer\n\n" +
                        "Developer ID: %s\n" +
                        "Total Tasks: %d\n" +
                        "Time Frame: Last %d days",
                response.getDeveloperId(),
                response.getTaskCount(),
                response.getSinceDays()
        );

        NotificationMessage notification = NotificationMessage.builder()
                .subject("Evaluation Report - Task Amount")
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaProducerService.sendEmailNotification(notification);
    }
}