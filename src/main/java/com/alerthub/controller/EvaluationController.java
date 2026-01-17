package com.alerthub.controller;

import com.alerthub.dto.DeveloperMostLabelResponse;
import com.alerthub.dto.ErrorResponse;
import com.alerthub.dto.LabelAggregateResponse;
import com.alerthub.dto.TaskAmountResponse;
import com.alerthub.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/evaluation")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Evaluation", description = "Evaluation API for analyzing developer performance and workload")
public class EvaluationController {

    private final EvaluationService evaluationService;

    @Operation(
            summary = "Find developer with most occurrences of a specific label",
            description = "Retrieves the developer with the highest number of tasks associated with a specific label within a given time frame. The result is also sent to the notifications queue."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully found the developer",
                    content = @Content(schema = @Schema(implementation = DeveloperMostLabelResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid parameter provided",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No tasks found with the specified label",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/developer/mostlabel")
    public ResponseEntity<DeveloperMostLabelResponse> getDeveloperWithMostLabel(
            @Parameter(
                    description = "The label to search for. Valid values: bug, documentation, duplicate, enhancement, good_first_issue, help_wanted, invalid, question, wontfix",
                    required = true,
                    example = "bug"
            )
            @RequestParam("label") String label,

            @Parameter(
                    description = "Number of days to look back from the current date",
                    required = true,
                    example = "7"
            )
            @RequestParam("since") Integer sinceDays) {

        log.info("GET /evaluation/developer/mostlabel - label: {}, since: {}", label, sinceDays);

        DeveloperMostLabelResponse response = evaluationService.findDeveloperWithMostLabel(label, sinceDays);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get label aggregate for a developer",
            description = "Returns the count of tasks associated with each label for the specified developer within a given time frame. The result is also sent to the notifications queue."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved label aggregate",
                    content = @Content(schema = @Schema(implementation = LabelAggregateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid parameter provided",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No tasks found for the specified developer",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/developer/{developer_id}/labelaggregate")
    public ResponseEntity<LabelAggregateResponse> getLabelAggregateByDeveloper(
            @Parameter(
                    description = "The ID of the developer",
                    required = true,
                    example = "dev123"
            )
            @PathVariable("developer_id") String developerId,

            @Parameter(
                    description = "Number of days to look back from the current date",
                    required = true,
                    example = "7"
            )
            @RequestParam("since") Integer sinceDays) {

        log.info("GET /evaluation/developer/{}/labelaggregate - since: {}", developerId, sinceDays);

        LabelAggregateResponse response = evaluationService.getLabelAggregateByDeveloper(developerId, sinceDays);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get total task amount for a developer",
            description = "Returns the total number of tasks assigned to a specified developer within a given time frame. The result is also sent to the notifications queue."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved task amount",
                    content = @Content(schema = @Schema(implementation = TaskAmountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid parameter provided",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No tasks found for the specified developer",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/developer/{developer_id}/task-amount")
    public ResponseEntity<TaskAmountResponse> getTaskAmountByDeveloper(
            @Parameter(
                    description = "The ID of the developer",
                    required = true,
                    example = "dev123"
            )
            @PathVariable("developer_id") String developerId,

            @Parameter(
                    description = "Number of days to look back from the current date",
                    required = true,
                    example = "7"
            )
            @RequestParam("since") Integer sinceDays) {

        log.info("GET /evaluation/developer/{}/task-amount - since: {}", developerId, sinceDays);

        TaskAmountResponse response = evaluationService.getTaskAmountByDeveloper(developerId, sinceDays);

        return ResponseEntity.ok(response);
    }
}