package org.loader.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.loader.model.enums.Provider;
import org.loader.service.LoaderService;
import org.loader.service.github.GitHubFileDownloader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST API endpoints for GitHub file operations.
 */
@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "GitHub", description = "GitHub file download and processing operations")
public class GitHubController {

    private final GitHubFileDownloader gitHubDownloader;
    private final LoaderService loaderService;

    /**
     * Download files from GitHub and process them.
     */
    @PostMapping("/download-and-process")
    @Operation(
            summary = "Download from GitHub and process",
            description = "Download all CSV files from GitHub repository and process them immediately"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Download and processing completed"),
            @ApiResponse(responseCode = "500", description = "Download or processing failed")
    })
    public ResponseEntity<Map<String, Object>> downloadAndProcess() {
        log.info("GitHub download and process triggered");

        try {
            long startTime = System.currentTimeMillis();

            // Step 1: Download files from GitHub
            int filesDownloaded = gitHubDownloader.downloadAllFiles();

            // Step 2: Process downloaded files
            int recordsProcessed = loaderService.scanAndProcessAll();

            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Download and processing completed successfully");
            response.put("filesDownloaded", filesDownloaded);
            response.put("recordsProcessed", recordsProcessed);
            response.put("durationMs", duration);

            log.info("GitHub download and process completed: {} files, {} records in {}ms",
                    filesDownloaded, recordsProcessed, duration);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("GitHub download and process failed", e);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Download and processing failed: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Download files from GitHub only (without processing).
     */
    @PostMapping("/download")
    @Operation(
            summary = "Download from GitHub",
            description = "Download all CSV files from GitHub repository to local data directory"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Download completed"),
            @ApiResponse(responseCode = "500", description = "Download failed")
    })
    public ResponseEntity<Map<String, Object>> downloadOnly() {
        log.info("GitHub download triggered");

        try {
            long startTime = System.currentTimeMillis();
            int filesDownloaded = gitHubDownloader.downloadAllFiles();
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Download completed successfully");
            response.put("filesDownloaded", filesDownloaded);
            response.put("durationMs", duration);

            log.info("GitHub download completed: {} files in {}ms", filesDownloaded, duration);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("GitHub download failed", e);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Download failed: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Download files for a specific provider from GitHub.
     */
    @PostMapping("/download/{provider}")
    @Operation(
            summary = "Download for specific provider",
            description = "Download CSV files from GitHub for a specific provider only"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Download completed"),
            @ApiResponse(responseCode = "400", description = "Invalid provider"),
            @ApiResponse(responseCode = "500", description = "Download failed")
    })
    public ResponseEntity<Map<String, Object>> downloadProvider(
            @Parameter(description = "Provider name (github, jira, or clickup)", required = true)
            @PathVariable String provider) {

        log.info("GitHub download triggered for provider: {}", provider);

        try {
            Provider providerEnum = Provider.fromString(provider);

            long startTime = System.currentTimeMillis();
            int filesDownloaded = gitHubDownloader.downloadProviderFiles(providerEnum);
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Download completed successfully");
            response.put("provider", providerEnum.getValue());
            response.put("filesDownloaded", filesDownloaded);
            response.put("durationMs", duration);

            log.info("GitHub download completed for {}: {} files in {}ms",
                    provider, filesDownloaded, duration);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid provider: {}", provider);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Invalid provider: " + provider);
            response.put("validProviders", new String[]{"github", "jira", "clickup"});

            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("GitHub download failed for provider: {}", provider, e);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Download failed: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}