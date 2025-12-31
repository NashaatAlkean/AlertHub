package org.loader.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

/**
 * DTO for parsing ClickUp CSV files.
 * Maps CSV columns to Java fields using OpenCSV annotations.
 *
 * CSV Column Names (from spec):
 * - owner_id
 * - project
 * - tag
 * - label
 * - worker_id
 * - task
 * - pr_env
 * - user_story
 * - day
 * - currant_sprint (note: typo in spec)
 */
@Data
public class ClickUpDataDto {

    @CsvBindByName(column = "owner_id")
    private String ownerId;

    @CsvBindByName(column = "project")
    private String project;

    @CsvBindByName(column = "tag")
    private String tag;

    @CsvBindByName(column = "label")
    private String label;

    @CsvBindByName(column = "worker_id")
    private String workerId;

    @CsvBindByName(column = "task")
    private String task;

    @CsvBindByName(column = "pr_env")
    private String prEnv;

    @CsvBindByName(column = "user_story")
    private String userStory;

    @CsvBindByName(column = "day")
    private String day;  // String to handle null/empty values

    @CsvBindByName(column = "currant_sprint")  // Note: Typo in spec
    private String currantSprint;
}