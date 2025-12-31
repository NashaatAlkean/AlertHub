package org.loader.model;

import org.loader.model.enums.ProcessStatus;
import org.loader.model.enums.Provider;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity for tracking which files have been processed by the loader.
 * Prevents duplicate processing of the same file.
 */
@Entity
@Table(name = "file_tracking",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_provider_filename", columnNames = {"provider", "filename"})
        },
        indexes = {
                @Index(name = "idx_provider", columnList = "provider"),
                @Index(name = "idx_processed_at", columnList = "processed_at"),
                @Index(name = "idx_status", columnList = "status")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Provider that generated the file (GitHub, Jira, ClickUp)
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Provider provider;

    /**
     * Name of the processed file
     * Format: {provider}_yyyy_MM_dd'T'HH_mm_ss
     * Example: jira_2024_08_22T13_30_00
     */
    @NotNull
    @Column(nullable = false, length = 255)
    private String filename;

    /**
     * Timestamp when the file was processed
     */
    @NotNull
    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    /**
     * Processing status (SUCCESS, FAILED, PROCESSING)
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProcessStatus status;

    /**
     * Number of records successfully processed from this file
     */
    @Column(name = "records_processed")
    private Integer recordsProcessed;

    /**
     * Error message if processing failed
     */
    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    /**
     * Pre-persist callback to set timestamp if not already set
     */
    @PrePersist
    protected void onCreate() {
        if (processedAt == null) {
            processedAt = LocalDateTime.now();
        }
        if (recordsProcessed == null) {
            recordsProcessed = 0;
        }
    }
}