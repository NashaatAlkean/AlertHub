package com.alerthub.controller;

import com.alerthub.dto.DeveloperMostLabelResponse;
import com.alerthub.dto.LabelAggregateResponse;
import com.alerthub.dto.TaskAmountResponse;
import com.alerthub.exception.InvalidParameterException;
import com.alerthub.exception.ResourceNotFoundException;
import com.alerthub.service.EvaluationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EvaluationController.class)
@DisplayName("EvaluationController Integration Tests")
class EvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EvaluationService evaluationService;

    @Nested
    @DisplayName("GET /evaluation/developer/mostlabel Tests")
    class GetDeveloperWithMostLabelTests {

        @Test
        @DisplayName("Should return 200 with developer data")
        void shouldReturn200WithDeveloperData() throws Exception {
            // Given
            DeveloperMostLabelResponse response = DeveloperMostLabelResponse.builder()
                    .developerId("dev123")
                    .label("bug")
                    .count(15L)
                    .sinceDays(7)
                    .build();

            when(evaluationService.findDeveloperWithMostLabel("bug", 7)).thenReturn(response);

            // When & Then
            mockMvc.perform(get("/evaluation/developer/mostlabel")
                            .param("label", "bug")
                            .param("since", "7")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.developerId").value("dev123"))
                    .andExpect(jsonPath("$.label").value("bug"))
                    .andExpect(jsonPath("$.count").value(15))
                    .andExpect(jsonPath("$.sinceDays").value(7));
        }

        @Test
        @DisplayName("Should return 404 when no tasks found")
        void shouldReturn404WhenNoTasksFound() throws Exception {
            // Given
            when(evaluationService.findDeveloperWithMostLabel("bug", 7))
                    .thenThrow(new ResourceNotFoundException("No tasks found with label 'bug' in the last 7 days"));

            // When & Then
            mockMvc.perform(get("/evaluation/developer/mostlabel")
                            .param("label", "bug")
                            .param("since", "7")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("No tasks found with label 'bug' in the last 7 days"));
        }

        @Test
        @DisplayName("Should return 400 for invalid label")
        void shouldReturn400ForInvalidLabel() throws Exception {
            // Given
            when(evaluationService.findDeveloperWithMostLabel("invalid_label", 7))
                    .thenThrow(new InvalidParameterException("Invalid label: 'invalid_label'"));

            // When & Then
            mockMvc.perform(get("/evaluation/developer/mostlabel")
                            .param("label", "invalid_label")
                            .param("since", "7")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid label: 'invalid_label'"));
        }

        @Test
        @DisplayName("Should return 400 when label parameter is missing")
        void shouldReturn400WhenLabelMissing() throws Exception {
            // When & Then
            mockMvc.perform(get("/evaluation/developer/mostlabel")
                            .param("since", "7")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when since parameter is missing")
        void shouldReturn400WhenSinceMissing() throws Exception {
            // When & Then
            mockMvc.perform(get("/evaluation/developer/mostlabel")
                            .param("label", "bug")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /evaluation/developer/{developer_id}/labelaggregate Tests")
    class GetLabelAggregateTests {

        @Test
        @DisplayName("Should return 200 with label aggregate data")
        void shouldReturn200WithLabelAggregateData() throws Exception {
            // Given
            Map<String, Long> labelCounts = new HashMap<>();
            labelCounts.put("bug", 10L);
            labelCounts.put("enhancement", 5L);

            LabelAggregateResponse response = LabelAggregateResponse.builder()
                    .developerId("dev123")
                    .labelCounts(labelCounts)
                    .sinceDays(7)
                    .totalTasks(15L)
                    .build();

            when(evaluationService.getLabelAggregateByDeveloper("dev123", 7)).thenReturn(response);

            // When & Then
            mockMvc.perform(get("/evaluation/developer/dev123/labelaggregate")
                            .param("since", "7")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.developerId").value("dev123"))
                    .andExpect(jsonPath("$.sinceDays").value(7))
                    .andExpect(jsonPath("$.totalTasks").value(15))
                    .andExpect(jsonPath("$.labelCounts.bug").value(10))
                    .andExpect(jsonPath("$.labelCounts.enhancement").value(5));
        }

        @Test
        @DisplayName("Should return 404 when no tasks found for developer")
        void shouldReturn404WhenNoTasksFoundForDeveloper() throws Exception {
            // Given
            when(evaluationService.getLabelAggregateByDeveloper("dev456", 7))
                    .thenThrow(new ResourceNotFoundException("No tasks found for developer 'dev456' in the last 7 days"));

            // When & Then
            mockMvc.perform(get("/evaluation/developer/dev456/labelaggregate")
                            .param("since", "7")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("No tasks found for developer 'dev456' in the last 7 days"));
        }

        @Test
        @DisplayName("Should return 400 when since parameter is missing")
        void shouldReturn400WhenSinceMissing() throws Exception {
            // When & Then
            mockMvc.perform(get("/evaluation/developer/dev123/labelaggregate")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /evaluation/developer/{developer_id}/task-amount Tests")
    class GetTaskAmountTests {

        @Test
        @DisplayName("Should return 200 with task amount data")
        void shouldReturn200WithTaskAmountData() throws Exception {
            // Given
            TaskAmountResponse response = TaskAmountResponse.builder()
                    .developerId("dev123")
                    .taskCount(25L)
                    .sinceDays(30)
                    .build();

            when(evaluationService.getTaskAmountByDeveloper("dev123", 30)).thenReturn(response);

            // When & Then
            mockMvc.perform(get("/evaluation/developer/dev123/task-amount")
                            .param("since", "30")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.developerId").value("dev123"))
                    .andExpect(jsonPath("$.taskCount").value(25))
                    .andExpect(jsonPath("$.sinceDays").value(30));
        }

        @Test
        @DisplayName("Should return 404 when no tasks found for developer")
        void shouldReturn404WhenNoTasksFoundForDeveloper() throws Exception {
            // Given
            when(evaluationService.getTaskAmountByDeveloper("dev789", 7))
                    .thenThrow(new ResourceNotFoundException("No tasks found for developer 'dev789' in the last 7 days"));

            // When & Then
            mockMvc.perform(get("/evaluation/developer/dev789/task-amount")
                            .param("since", "7")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("No tasks found for developer 'dev789' in the last 7 days"));
        }

        @Test
        @DisplayName("Should return 400 when since parameter is missing")
        void shouldReturn400WhenSinceMissing() throws Exception {
            // When & Then
            mockMvc.perform(get("/evaluation/developer/dev123/task-amount")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 for invalid since parameter")
        void shouldReturn400ForInvalidSinceParameter() throws Exception {
            // Given
            when(evaluationService.getTaskAmountByDeveloper(anyString(), anyInt()))
                    .thenThrow(new InvalidParameterException("Since days must be a positive integer"));

            // When & Then
            mockMvc.perform(get("/evaluation/developer/dev123/task-amount")
                            .param("since", "-5")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }
}