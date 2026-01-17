package com.alerthub.service;

import com.alerthub.dto.DeveloperMostLabelResponse;
import com.alerthub.dto.LabelAggregateResponse;
import com.alerthub.dto.TaskAmountResponse;
import com.alerthub.exception.InvalidParameterException;
import com.alerthub.exception.ResourceNotFoundException;
import com.alerthub.repository.PlatformInformationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EvaluationService Tests")
class EvaluationServiceTest {

    @Mock
    private PlatformInformationRepository platformInformationRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private EvaluationServiceImpl evaluationService;

    @Nested
    @DisplayName("findDeveloperWithMostLabel Tests")
    class FindDeveloperWithMostLabelTests {

        @Test
        @DisplayName("Should return developer with most occurrences of a label")
        void shouldReturnDeveloperWithMostLabel() {
            // Given
            String label = "bug";
            Integer sinceDays = 7;
            List<Object[]> mockResults = new ArrayList<>();
            mockResults.add(new Object[]{"dev123", 15L});

            when(platformInformationRepository.findDeveloperWithMostLabel(eq(label), any(LocalDateTime.class)))
                    .thenReturn(mockResults);
            doNothing().when(kafkaProducerService).sendEmailNotification(any());

            // When
            DeveloperMostLabelResponse response = evaluationService.findDeveloperWithMostLabel(label, sinceDays);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getDeveloperId()).isEqualTo("dev123");
            assertThat(response.getLabel()).isEqualTo("bug");
            assertThat(response.getCount()).isEqualTo(15L);
            assertThat(response.getSinceDays()).isEqualTo(7);

            verify(platformInformationRepository, times(1))
                    .findDeveloperWithMostLabel(eq(label), any(LocalDateTime.class));
            verify(kafkaProducerService, times(1)).sendEmailNotification(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when no tasks found")
        void shouldThrowResourceNotFoundExceptionWhenNoTasksFound() {
            // Given
            String label = "bug";
            Integer sinceDays = 7;

            when(platformInformationRepository.findDeveloperWithMostLabel(eq(label), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());

            // When & Then
            assertThatThrownBy(() -> evaluationService.findDeveloperWithMostLabel(label, sinceDays))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("No tasks found with label 'bug' in the last 7 days");

            verify(kafkaProducerService, never()).sendEmailNotification(any());
        }

        @Test
        @DisplayName("Should throw InvalidParameterException for null label")
        void shouldThrowInvalidParameterExceptionForNullLabel() {
            // Given
            String label = null;
            Integer sinceDays = 7;

            // When & Then
            assertThatThrownBy(() -> evaluationService.findDeveloperWithMostLabel(label, sinceDays))
                    .isInstanceOf(InvalidParameterException.class)
                    .hasMessageContaining("Label parameter cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw InvalidParameterException for empty label")
        void shouldThrowInvalidParameterExceptionForEmptyLabel() {
            // Given
            String label = "";
            Integer sinceDays = 7;

            // When & Then
            assertThatThrownBy(() -> evaluationService.findDeveloperWithMostLabel(label, sinceDays))
                    .isInstanceOf(InvalidParameterException.class)
                    .hasMessageContaining("Label parameter cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw InvalidParameterException for invalid label")
        void shouldThrowInvalidParameterExceptionForInvalidLabel() {
            // Given
            String label = "invalid_label_xyz";
            Integer sinceDays = 7;

            // When & Then
            assertThatThrownBy(() -> evaluationService.findDeveloperWithMostLabel(label, sinceDays))
                    .isInstanceOf(InvalidParameterException.class)
                    .hasMessageContaining("Invalid label");
        }

        @Test
        @DisplayName("Should throw InvalidParameterException for null sinceDays")
        void shouldThrowInvalidParameterExceptionForNullSinceDays() {
            // Given
            String label = "bug";
            Integer sinceDays = null;

            // When & Then
            assertThatThrownBy(() -> evaluationService.findDeveloperWithMostLabel(label, sinceDays))
                    .isInstanceOf(InvalidParameterException.class)
                    .hasMessageContaining("Since days must be a positive integer");
        }

        @Test
        @DisplayName("Should throw InvalidParameterException for negative sinceDays")
        void shouldThrowInvalidParameterExceptionForNegativeSinceDays() {
            // Given
            String label = "bug";
            Integer sinceDays = -5;

            // When & Then
            assertThatThrownBy(() -> evaluationService.findDeveloperWithMostLabel(label, sinceDays))
                    .isInstanceOf(InvalidParameterException.class)
                    .hasMessageContaining("Since days must be a positive integer");
        }

        @Test
        @DisplayName("Should throw InvalidParameterException for zero sinceDays")
        void shouldThrowInvalidParameterExceptionForZeroSinceDays() {
            // Given
            String label = "bug";
            Integer sinceDays = 0;

            // When & Then
            assertThatThrownBy(() -> evaluationService.findDeveloperWithMostLabel(label, sinceDays))
                    .isInstanceOf(InvalidParameterException.class)
                    .hasMessageContaining("Since days must be a positive integer");
        }

        @Test
        @DisplayName("Should accept all valid labels")
        void shouldAcceptAllValidLabels() {
            // Given
            List<String> validLabels = Arrays.asList(
                    "bug", "documentation", "duplicate", "enhancement",
                    "good_first_issue", "help_wanted", "invalid", "question", "wontfix"
            );

            List<Object[]> mockResults = new ArrayList<>();
            mockResults.add(new Object[]{"dev123", 5L});

            when(platformInformationRepository.findDeveloperWithMostLabel(anyString(), any(LocalDateTime.class)))
                    .thenReturn(mockResults);
            doNothing().when(kafkaProducerService).sendEmailNotification(any());

            // When & Then
            for (String label : validLabels) {
                DeveloperMostLabelResponse response = evaluationService.findDeveloperWithMostLabel(label, 7);
                assertThat(response).isNotNull();
                assertThat(response.getLabel()).isEqualTo(label);
            }
        }
    }

    @Nested
    @DisplayName("getLabelAggregateByDeveloper Tests")
    class GetLabelAggregateByDeveloperTests {

        @Test
        @DisplayName("Should return label aggregate for developer")
        void shouldReturnLabelAggregateForDeveloper() {
            // Given
            String developerId = "dev123";
            Integer sinceDays = 7;
            List<Object[]> mockResults = new ArrayList<>();
            mockResults.add(new Object[]{"bug", 10L});
            mockResults.add(new Object[]{"enhancement", 5L});
            mockResults.add(new Object[]{"documentation", 3L});

            when(platformInformationRepository.findLabelAggregateByDeveloper(eq(developerId), any(LocalDateTime.class)))
                    .thenReturn(mockResults);
            doNothing().when(kafkaProducerService).sendEmailNotification(any());

            // When
            LabelAggregateResponse response = evaluationService.getLabelAggregateByDeveloper(developerId, sinceDays);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getDeveloperId()).isEqualTo("dev123");
            assertThat(response.getSinceDays()).isEqualTo(7);
            assertThat(response.getTotalTasks()).isEqualTo(18L);
            assertThat(response.getLabelCounts()).hasSize(3);
            assertThat(response.getLabelCounts().get("bug")).isEqualTo(10L);
            assertThat(response.getLabelCounts().get("enhancement")).isEqualTo(5L);
            assertThat(response.getLabelCounts().get("documentation")).isEqualTo(3L);

            verify(platformInformationRepository, times(1))
                    .findLabelAggregateByDeveloper(eq(developerId), any(LocalDateTime.class));
            verify(kafkaProducerService, times(1)).sendEmailNotification(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when no tasks found for developer")
        void shouldThrowResourceNotFoundExceptionWhenNoTasksForDeveloper() {
            // Given
            String developerId = "dev456";
            Integer sinceDays = 7;

            when(platformInformationRepository.findLabelAggregateByDeveloper(eq(developerId), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());

            // When & Then
            assertThatThrownBy(() -> evaluationService.getLabelAggregateByDeveloper(developerId, sinceDays))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("No tasks found for developer 'dev456' in the last 7 days");

            verify(kafkaProducerService, never()).sendEmailNotification(any());
        }

        @Test
        @DisplayName("Should throw InvalidParameterException for null developerId")
        void shouldThrowInvalidParameterExceptionForNullDeveloperId() {
            // Given
            String developerId = null;
            Integer sinceDays = 7;

            // When & Then
            assertThatThrownBy(() -> evaluationService.getLabelAggregateByDeveloper(developerId, sinceDays))
                    .isInstanceOf(InvalidParameterException.class)
                    .hasMessageContaining("Developer ID cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw InvalidParameterException for empty developerId")
        void shouldThrowInvalidParameterExceptionForEmptyDeveloperId() {
            // Given
            String developerId = "   ";
            Integer sinceDays = 7;

            // When & Then
            assertThatThrownBy(() -> evaluationService.getLabelAggregateByDeveloper(developerId, sinceDays))
                    .isInstanceOf(InvalidParameterException.class)
                    .hasMessageContaining("Developer ID cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw InvalidParameterException for invalid sinceDays")
        void shouldThrowInvalidParameterExceptionForInvalidSinceDays() {
            // Given
            String developerId = "dev123";
            Integer sinceDays = -1;

            // When & Then
            assertThatThrownBy(() -> evaluationService.getLabelAggregateByDeveloper(developerId, sinceDays))
                    .isInstanceOf(InvalidParameterException.class)
                    .hasMessageContaining("Since days must be a positive integer");
        }
    }

    @Nested
    @DisplayName("getTaskAmountByDeveloper Tests")
    class GetTaskAmountByDeveloperTests {

        @Test
        @DisplayName("Should return task amount for developer")
        void shouldReturnTaskAmountForDeveloper() {
            // Given
            String developerId = "dev123";
            Integer sinceDays = 30;
            Long expectedCount = 25L;

            when(platformInformationRepository.countTasksByDeveloper(eq(developerId), any(LocalDateTime.class)))
                    .thenReturn(expectedCount);
            doNothing().when(kafkaProducerService).sendEmailNotification(any());

            // When
            TaskAmountResponse response = evaluationService.getTaskAmountByDeveloper(developerId, sinceDays);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getDeveloperId()).isEqualTo("dev123");
            assertThat(response.getTaskCount()).isEqualTo(25L);
            assertThat(response.getSinceDays()).isEqualTo(30);

            verify(platformInformationRepository, times(1))
                    .countTasksByDeveloper(eq(developerId), any(LocalDateTime.class));
            verify(kafkaProducerService, times(1)).sendEmailNotification(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when task count is zero")
        void shouldThrowResourceNotFoundExceptionWhenTaskCountIsZero() {
            // Given
            String developerId = "dev789";
            Integer sinceDays = 7;

            when(platformInformationRepository.countTasksByDeveloper(eq(developerId), any(LocalDateTime.class)))
                    .thenReturn(0L);

            // When & Then
            assertThatThrownBy(() -> evaluationService.getTaskAmountByDeveloper(developerId, sinceDays))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("No tasks found for developer 'dev789' in the last 7 days");

            verify(kafkaProducerService, never()).sendEmailNotification(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when task count is null")
        void shouldThrowResourceNotFoundExceptionWhenTaskCountIsNull() {
            // Given
            String developerId = "dev789";
            Integer sinceDays = 7;

            when(platformInformationRepository.countTasksByDeveloper(eq(developerId), any(LocalDateTime.class)))
                    .thenReturn(null);

            // When & Then
            assertThatThrownBy(() -> evaluationService.getTaskAmountByDeveloper(developerId, sinceDays))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("No tasks found for developer 'dev789' in the last 7 days");

            verify(kafkaProducerService, never()).sendEmailNotification(any());
        }

        @Test
        @DisplayName("Should throw InvalidParameterException for null developerId")
        void shouldThrowInvalidParameterExceptionForNullDeveloperId() {
            // Given
            String developerId = null;
            Integer sinceDays = 7;

            // When & Then
            assertThatThrownBy(() -> evaluationService.getTaskAmountByDeveloper(developerId, sinceDays))
                    .isInstanceOf(InvalidParameterException.class)
                    .hasMessageContaining("Developer ID cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw InvalidParameterException for invalid sinceDays")
        void shouldThrowInvalidParameterExceptionForInvalidSinceDays() {
            // Given
            String developerId = "dev123";
            Integer sinceDays = 0;

            // When & Then
            assertThatThrownBy(() -> evaluationService.getTaskAmountByDeveloper(developerId, sinceDays))
                    .isInstanceOf(InvalidParameterException.class)
                    .hasMessageContaining("Since days must be a positive integer");
        }
    }

    @Nested
    @DisplayName("Kafka Notification Tests")
    class KafkaNotificationTests {

        @Test
        @DisplayName("Should send notification after successful findDeveloperWithMostLabel")
        void shouldSendNotificationAfterFindDeveloperWithMostLabel() {
            // Given
            String label = "bug";
            Integer sinceDays = 7;
            List<Object[]> mockResults = new ArrayList<>();
            mockResults.add(new Object[]{"dev123", 15L});

            when(platformInformationRepository.findDeveloperWithMostLabel(eq(label), any(LocalDateTime.class)))
                    .thenReturn(mockResults);
            doNothing().when(kafkaProducerService).sendEmailNotification(any());

            // When
            evaluationService.findDeveloperWithMostLabel(label, sinceDays);

            // Then
            verify(kafkaProducerService, times(1)).sendEmailNotification(any());
        }

        @Test
        @DisplayName("Should send notification after successful getLabelAggregateByDeveloper")
        void shouldSendNotificationAfterGetLabelAggregateByDeveloper() {
            // Given
            String developerId = "dev123";
            Integer sinceDays = 7;
            List<Object[]> mockResults = new ArrayList<>();
            mockResults.add(new Object[]{"bug", 10L});

            when(platformInformationRepository.findLabelAggregateByDeveloper(eq(developerId), any(LocalDateTime.class)))
                    .thenReturn(mockResults);
            doNothing().when(kafkaProducerService).sendEmailNotification(any());

            // When
            evaluationService.getLabelAggregateByDeveloper(developerId, sinceDays);

            // Then
            verify(kafkaProducerService, times(1)).sendEmailNotification(any());
        }

        @Test
        @DisplayName("Should send notification after successful getTaskAmountByDeveloper")
        void shouldSendNotificationAfterGetTaskAmountByDeveloper() {
            // Given
            String developerId = "dev123";
            Integer sinceDays = 7;

            when(platformInformationRepository.countTasksByDeveloper(eq(developerId), any(LocalDateTime.class)))
                    .thenReturn(25L);
            doNothing().when(kafkaProducerService).sendEmailNotification(any());

            // When
            evaluationService.getTaskAmountByDeveloper(developerId, sinceDays);

            // Then
            verify(kafkaProducerService, times(1)).sendEmailNotification(any());
        }
    }
}