package org.loader.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

/**
 * DTO for parsing GitHub CSV files.
 * Maps CSV columns to Java fields using OpenCSV annotations.
 *
 * CSV Column Names (from spec):
 * - manager_id
 * - projects
 * - assignee
 * - label
 * - devloper_id (note: typo in spec)
 * - issue
 * - environment
 * - user_story
 * - point
 * - sprint
 */
@Data
public class GitHubDataDto {

    @CsvBindByName(column = "manager_id")
    private String managerId;

    @CsvBindByName(column = "projects")
    private String projects;

    @CsvBindByName(column = "assignee")
    private String assignee;

    @CsvBindByName(column = "label")
    private String label;

    @CsvBindByName(column = "devloper_id")  // Note: Typo in spec
    private String devloperId;

    @CsvBindByName(column = "issue")
    private String issue;

    @CsvBindByName(column = "environment")
    private String environment;

    @CsvBindByName(column = "user_story")
    private String userStory;

    @CsvBindByName(column = "point")
    private String point;  // String to handle null/empty values

    @CsvBindByName(column = "sprint")
    private String sprint;
}