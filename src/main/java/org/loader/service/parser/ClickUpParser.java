package org.loader.service.parser;

import org.loader.dto.ClickUpDataDto;
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
 * Parser for ClickUp CSV files.
 * Converts ClickUp-specific CSV format to PlatformInformation entities.
 */
@Component
@Slf4j
public class ClickUpParser implements DataParser {

    @Override
    public List<PlatformInformation> parse(File file) {
        log.info("Parsing ClickUp file: {}", file.getName());

        try (FileReader reader = new FileReader(file)) {
            // Parse CSV using OpenCSV
            List<ClickUpDataDto> dtos = new CsvToBeanBuilder<ClickUpDataDto>(reader)
                    .withType(ClickUpDataDto.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build()
                    .parse();

            log.info("Found {} records in ClickUp file: {}", dtos.size(), file.getName());

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

            log.info("Successfully transformed {} out of {} records from ClickUp file",
                    entities.size(), dtos.size());

            return entities;

        } catch (Exception e) {
            log.error("Failed to parse ClickUp file: {}", file.getName(), e);
            throw new FileParsingException(
                    "Failed to parse ClickUp file: " + e.getMessage(),
                    file.getName(),
                    e
            );
        }
    }

    /**
     * Transform ClickUp DTO to PlatformInformation entity.
     * Maps ClickUp-specific field names to database columns.
     */
    private PlatformInformation transform(ClickUpDataDto dto) {
        // Parse label
        Label label = Label.fromString(dto.getLabel());
        if (label == null && dto.getLabel() != null && !dto.getLabel().trim().isEmpty()) {
            throw new InvalidLabelException(
                    "Invalid label value: " + dto.getLabel(),
                    dto.getLabel()
            );
        }

        // Parse task points (handle null/empty) - ClickUp uses "day" field
        Integer taskPoint = parseInteger(dto.getDay());

        return PlatformInformation.builder()
                .timestamp(LocalDateTime.now())
                .ownerId(dto.getOwnerId())           // owner_id → owner_id
                .project(dto.getProject())           // project → project
                .tag(dto.getTag())                   // tag → tag
                .label(label)                        // label → label (enum)
                .developerId(dto.getWorkerId())      // worker_id → developer_id
                .taskNumber(dto.getTask())           // task → task_number
                .environment(dto.getPrEnv())         // pr_env → environment
                .userStory(dto.getUserStory())       // user_story → user_story
                .taskPoint(taskPoint)                // day → task_point (null → 0)
                .sprint(dto.getCurrantSprint())      // currant_sprint → sprint
                .provider(Provider.CLICKUP)          // Set provider
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
        return Provider.CLICKUP;
    }
}