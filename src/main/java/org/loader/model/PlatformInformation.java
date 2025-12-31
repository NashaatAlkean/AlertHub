package org.loader.model;

import org.loader.model.enums.Label;
import org.loader.model.enums.Provider;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * Entity representing platform information from various providers (GitHub, Jira, ClickUp).
 * This table stores the unified, transformed data from all providers.
 */
@Entity
@Table(name = "platform_information", indexes = {
        @Index(name = "idx_owner_id", columnList = "owner_id"),
        @Index(name = "idx_developer_id", columnList = "developer_id"),
        @Index(name = "idx_label", columnList = "label"),
        @Index(name = "idx_timestamp", columnList = "timestamp"),
        @Index(name = "idx_project", columnList = "project"),
        @Index(name = "idx_provider", columnList = "provider")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"userStory"})
@ToString(exclude = {"userStory"})
@Accessors(chain = true)
public class PlatformInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Timestamp when the data was scanned/loaded into the system
     */
    @NotNull
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    /**
     * Owner/Manager ID of the project
     * Mapped from: manager_id (GitHub), owner_id (ClickUp), manager_id (Jira)
     */
    @Column(name = "owner_id", length = 100)
    private String ownerId;

    /**
     * Project name
     * Mapped from: projects (GitHub), project (ClickUp), projects (Jira)
     */
    @Column(name = "project", length = 200)
    private String project;

    /**
     * Tag/assignee information
     * Mapped from: assignee (GitHub), tag (ClickUp), assignee (Jira)
     */
    @Column(name = "tag", length = 100)
    private String tag;

    /**
     * Label categorizing the type of work
     * Mapped from: label (all providers)
     * IMPORTANT: Must be one of the 9 predefined label types (enum)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "label", length = 50)
    private Label label;

    /**
     * Developer/worker ID assigned to the task
     * Mapped from: devloper_id (GitHub), worker_id (ClickUp), employeeID (Jira)
     */
    @Column(name = "developer_id", length = 100)
    private String developerId;

    /**
     * Task/issue number
     * Mapped from: issue (GitHub), task (ClickUp), issue (Jira)
     */
    @Column(name = "task_number", length = 100)
    private String taskNumber;

    /**
     * Environment information
     * Mapped from: environment (GitHub), pr_env (ClickUp), env (Jira)
     */
    @Column(name = "environment", length = 100)
    private String environment;

    /**
     * User story reference
     * Mapped from: user_story (all providers)
     * Using TEXT type to support long descriptions
     */
    @Column(name = "user_story", columnDefinition = "TEXT")
    private String userStory;

    /**
     * Task points/estimation
     * Mapped from: point (GitHub), day (ClickUp), point (Jira)
     * Note: Null values are treated as 0
     */
    @Column(name = "task_point")
    private Integer taskPoint;

    /**
     * Sprint information
     * Mapped from: sprint (GitHub), currant_sprint (ClickUp), sprint (Jira)
     */
    @Column(name = "sprint", length = 100)
    private String sprint;

    /**
     * Provider that generated this data
     * Indicates whether data came from GitHub, Jira, or ClickUp
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "provider", length = 50)
    private Provider provider;

    /**
     * Pre-persist callback to set default values
     */
    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        // Ensure taskPoint is never null (treat null as 0)
        if (taskPoint == null) {
            taskPoint = 0;
        }
    }
}