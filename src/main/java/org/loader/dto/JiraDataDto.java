package org.loader.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

/**
 * DTO for parsing Jira CSV files.
 * Maps CSV columns to Java fields using OpenCSV annotations.
 *
 * CSV Column Names (from spec):
 * - manager_id
 * - projects
 * - assignee
 * - label
 * - employeeID
 * - issue
 * - env
 * - user_story
 * - point
 * - sprint
 */
@Data
public class JiraDataDto {

    @CsvBindByName(column = "manager_id")
    private String managerId;

    @CsvBindByName(column = "projects")
    private String projects;

    @CsvBindByName(column = "assignee")
    private String assignee;

    @CsvBindByName(column = "label")
    private String label;

    @CsvBindByName(column = "employeeID")
    private String employeeId;

    @CsvBindByName(column = "issue")
    private String issue;

    @CsvBindByName(column = "env")
    private String env;

    @CsvBindByName(column = "user_story")
    private String userStory;

    @CsvBindByName(column = "point")
    private String point;  // String to handle null/empty values

    @CsvBindByName(column = "sprint")
    private String sprint;
}