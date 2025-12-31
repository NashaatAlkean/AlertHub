package org.loader.controller;

import org.loader.model.FileTracking;
import org.loader.model.enums.ProcessStatus;
import org.loader.model.enums.Provider;
import org.loader.service.LoaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST API endpoints for the Loader microservice.
 * Provides manual triggers for file scanning and processing.
 */
@RestController
@RequestMapping("/api/loader")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Loader", description = "File loading and processing operations")
public class LoaderController {

    private final LoaderService loaderService;

    /**
     * Manually trigger scan and process for all providers.
     */
    @PostMapping("/scan")
    @Operation(
            summary = "Scan all providers",
            description = "Manually trigger file scanning and processing for all providers (GitHub, Jira, ClickUp)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Scan completed successfully"),
            @ApiResponse(responseCode = "500", description = "Scan failed")
    })
    public ResponseEntity<Map<String, Object>> scanAll() {
        log.info("Manual scan triggered for all providers");

        try {
            long startTime = System.currentTimeMillis();
            int totalRecords = loaderService.scanAndProcessAll();
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Scan completed successfully");
            response.put("totalRecords", totalRecords);
            response.put("durationMs", duration);

            log.info("Manual scan completed: {} records in {}ms", totalRecords, duration);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Manual scan failed", e);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Scan failed: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Manually trigger scan and process for a specific provider.
     */
    @PostMapping("/scan/{provider}")
    @Operation(
            summary = "Scan specific provider",
            description = "Manually trigger file scanning and processing for a specific provider"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Scan completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid provider"),
            @ApiResponse(responseCode = "500", description = "Scan failed")
    })
    public ResponseEntity<Map<String, Object>> scanProvider(
            @Parameter(description = "Provider name (github, jira, or clickup)", required = true)
            @PathVariable String provider) {

        log.info("Manual scan triggered for provider: {}", provider);

        try {
            Provider providerEnum = Provider.fromString(provider);

            long startTime = System.currentTimeMillis();
            int totalRecords = loaderService.scanAndProcessProvider(providerEnum);
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Scan completed successfully");
            response.put("provider", providerEnum.getValue());
            response.put("totalRecords", totalRecords);
            response.put("durationMs", duration);

            log.info("Manual scan completed for {}: {} records in {}ms",
                    provider, totalRecords, duration);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid provider: {}", provider);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Invalid provider: " + provider);
            response.put("validProviders", new String[]{"github", "jira", "clickup"});

            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("Manual scan failed for provider: {}", provider, e);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Scan failed: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get current processing status for all providers.
     */
    @GetMapping("/status")
    @Operation(
            summary = "Get loader status",
            description = "Get processing statistics for all providers"
    )
    @ApiResponse(responseCode = "200", description = "Status retrieved successfully")
    public ResponseEntity<Map<String, Object>> getStatus() {
        log.debug("Status request received");

        Map<String, Object> response = new HashMap<>();

        for (Provider provider : Provider.values()) {
            Map<String, Object> providerInfo = new HashMap<>();

            Map<ProcessStatus, Long> stats = loaderService.getProviderStatistics(provider);
            providerInfo.put("statistics", stats);

            FileTracking latest = loaderService.getLatestProcessing(provider);
            if (latest != null) {
                Map<String, Object> latestInfo = new HashMap<>();
                latestInfo.put("filename", latest.getFilename());
                latestInfo.put("status", latest.getStatus());
                latestInfo.put("processedAt", latest.getProcessedAt());
                latestInfo.put("recordsProcessed", latest.getRecordsProcessed());
                providerInfo.put("latestProcessing", latestInfo);
            }

            response.put(provider.getValue(), providerInfo);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Get processing history for a specific provider.
     */
    @GetMapping("/history/{provider}")
    @Operation(
            summary = "Get processing history",
            description = "Get file processing history for a specific provider"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "History retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid provider")
    })
    public ResponseEntity<Map<String, Object>> getHistory(
            @Parameter(description = "Provider name (github, jira, or clickup)", required = true)
            @PathVariable String provider) {

        try {
            Provider providerEnum = Provider.fromString(provider);

            Map<String, Object> response = new HashMap<>();
            response.put("provider", providerEnum.getValue());
            response.put("statistics", loaderService.getProviderStatistics(providerEnum));

            FileTracking latest = loaderService.getLatestProcessing(providerEnum);
            if (latest != null) {
                response.put("latestProcessing", latest);
            }

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Invalid provider: " + provider);
            response.put("validProviders", new String[]{"github", "jira", "clickup"});

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    @Operation(
            summary = "Health check",
            description = "Check if the Loader service is running"
    )
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Loader");
        return ResponseEntity.ok(response);
    }
}