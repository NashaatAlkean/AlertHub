package org.loader.service.parser;

import org.loader.dto.JiraDataDto;
import org.loader.exception.FileParsingException;
import org.loader.exception.InvalidLabelException;
import org.loader.model.PlatformInformation;
import org.loader.model.enums.Label;
import org.loader.model.enums.Provider;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for Jira CSV files.
 * Converts Jira-specific CSV format to PlatformInformation entities.
 */
@Component
@Slf4j
public class JiraParser implements DataParser {

    @Override
    public List<PlatformInformation> parse(File file) {
        log.info("Parsing Jira file: {}", file.getName());

        try (FileReader reader = new FileReader(file)) {
            // Parse CSV using OpenCSV
            List<JiraDataDto> dtos = new CsvToBeanBuilder<JiraDataDto>(reader)
                    .withType(JiraDataDto.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build()
                    .parse();

            log.info("Found {} records in Jira file: {}", dtos.size(), file.getName());

            // Transform DTOs to entities
            List<PlatformInformation> entities = new ArrayList<>();
            for (int i = 0; i < dtos.size(); i++) {
                try {
                    PlatformInformation entity = transform(dtos.get(i));
                    entities.add(entity);
                } catch (InvalidLabelException e) {
                    log.warn("Skipping record {} in file {} due to invalid label: {}",
                            i + 1, file.getName(), e.getInvalidLabel());
                    // Continue processing other records
                }
            }

            log.info("Successfully transformed {} out of {} records from Jira file",
                    entities.size(), dtos.size());

            return entities;

        } catch (Exception e) {
            log.error("Failed to parse Jira file: {}", file.getName(), e);
            throw new FileParsingException(
                    "Failed to parse Jira file: " + e.getMessage(),
                    file.getName(),
                    e
            );
        }
    }

    /**
     * Transform Jira DTO to PlatformInformation entity.
     * Maps Jira-specific field names to database columns.
     */
    private PlatformInformation transform(JiraDataDto dto) {
        // Parse label
        Label label = Label.fromString(dto.getLabel());
        if (label == null && dto.getLabel() != null && !dto.getLabel().trim().isEmpty()) {
            throw new InvalidLabelException(
                    "Invalid label value: " + dto.getLabel(),
                    dto.getLabel()
            );
        }

        // Parse task points (handle null/empty)
        Integer taskPoint = parseInteger(dto.getPoint());

        return PlatformInformation.builder()
                .timestamp(LocalDateTime.now())
                .ownerId(dto.getManagerId())         // manager_id → owner_id
                .project(dto.getProjects())          // projects → project
                .tag(dto.getAssignee())              // assignee → tag
                .label(label)                        // label → label (enum)
                .developerId(dto.getEmployeeId())    // employeeID → developer_id
                .taskNumber(dto.getIssue())          // issue → task_number
                .environment(dto.getEnv())           // env → environment
                .userStory(dto.getUserStory())       // user_story → user_story
                .taskPoint(taskPoint)                // point → task_point (null → 0)
                .sprint(dto.getSprint())             // sprint → sprint
                .provider(Provider.JIRA)             // Set provider
                .build();
    }

    /**
     * Parse integer from string, returning 0 for null/empty values.
     */
    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            log.warn("Invalid integer value: {}, defaulting to 0", value);
            return 0;
        }
    }

    @Override
    public Provider getProvider() {
        return Provider.JIRA;
    }
}