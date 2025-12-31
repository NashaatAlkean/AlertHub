package org.loader.service.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.loader.config.GitHubConfig;
import org.loader.config.LoaderConfig;
import org.loader.model.enums.Provider;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for downloading CSV files from GitHub repository.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubFileDownloader {

    private final GitHubConfig gitHubConfig;
    private final LoaderConfig loaderConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Download all CSV files from GitHub for all providers.
     *
     * @return number of files downloaded
     */
    public int downloadAllFiles() {
        log.info("Starting GitHub file download from repository: {}", gitHubConfig.getRepoPath());

        int totalDownloaded = 0;

        for (Provider provider : Provider.values()) {
            try {
                int downloaded = downloadProviderFiles(provider);
                totalDownloaded += downloaded;
            } catch (Exception e) {
                log.error("Failed to download files for provider: {}", provider.getValue(), e);
            }
        }

        log.info("GitHub download completed: {} files downloaded", totalDownloaded);

        return totalDownloaded;
    }

    /**
     * Download CSV files from GitHub for a specific provider.
     *
     * @param provider the provider (GitHub, Jira, or ClickUp)
     * @return number of files downloaded
     */
    public int downloadProviderFiles(Provider provider) {
        String folderPath = getProviderFolderPath(provider);

        log.info("=== Starting download for provider: {} ===", provider.getValue());
        log.info("GitHub folder path: {}", folderPath);

        try {
            // Get list of files in the folder
            List<String> fileNames = listFilesInGitHubFolder(folderPath);

            log.info("Found {} files in GitHub folder: {}", fileNames.size(), folderPath);

            if (fileNames.isEmpty()) {
                log.warn("No files found in GitHub folder: {}", folderPath);
                return 0;
            }

            // Log all found files
            for (String fileName : fileNames) {
                log.info("Found file in GitHub: {}", fileName);
            }

            // Create local directory if it doesn't exist
            String localDir = loaderConfig.getProviderDirectory(provider);
            log.info("Local directory: {}", localDir);
            Files.createDirectories(Paths.get(localDir));

            int downloaded = 0;
            int skipped = 0;

            for (String fileName : fileNames) {
                if (fileName.endsWith(".csv")) {
                    try {
                        // Remove .csv extension for local file
                        String localFileName = fileName.replace(".csv", "");
                        log.info("Processing: {} -> {}", fileName, localFileName);

                        boolean success = downloadFile(folderPath, fileName, localDir, localFileName);
                        if (success) {
                            downloaded++;
                            log.info("✅ Downloaded: {}", localFileName);
                        } else {
                            skipped++;
                            log.info("⏭️ Skipped (already exists): {}", localFileName);
                        }
                    } catch (Exception e) {
                        log.error("❌ Failed to download file: {}", fileName, e);
                    }
                } else {
                    log.debug("Skipping non-CSV file: {}", fileName);
                }
            }

            log.info("=== Download summary for {}: {} downloaded, {} skipped ===",
                    provider.getValue(), downloaded, skipped);

            return downloaded;

        } catch (Exception e) {
            log.error("=== Failed to download {} files from GitHub ===", provider.getValue(), e);
            return 0;
        }
    }

    /**
     * List files in a GitHub folder using GitHub API.
     */
    private List<String> listFilesInGitHubFolder(String folderPath) throws Exception {
        List<String> fileNames = new ArrayList<>();

        String apiUrl = gitHubConfig.getContentsApiUrl(folderPath);

        log.info("Calling GitHub API: {}", apiUrl);

        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
        conn.setRequestProperty("User-Agent", "Loader-Microservice");

        // Add token if provided (for private repos or higher rate limits)
        if (gitHubConfig.getToken() != null && !gitHubConfig.getToken().isEmpty()) {
            conn.setRequestProperty("Authorization", "token " + gitHubConfig.getToken());
            log.debug("Using GitHub token for authentication");
        } else {
            log.warn("No GitHub token provided - may hit rate limits (60 requests/hour)");
        }

        int responseCode = conn.getResponseCode();
        log.info("GitHub API response code: {}", responseCode);

        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String jsonResponse = response.toString();
            log.debug("GitHub API JSON response: {}", jsonResponse.substring(0, Math.min(500, jsonResponse.length())));

            // Parse JSON response
            JsonNode files = objectMapper.readTree(jsonResponse);

            if (files.isArray()) {
                log.info("GitHub returned {} items", files.size());

                for (JsonNode file : files) {
                    String fileName = file.get("name").asText();
                    String type = file.get("type").asText();

                    log.debug("GitHub item: {} (type: {})", fileName, type);

                    if ("file".equals(type)) {
                        fileNames.add(fileName);
                        log.info("✅ Added file: {}", fileName);
                    } else {
                        log.debug("Skipping non-file: {} (type: {})", fileName, type);
                    }
                }
            } else {
                log.error("GitHub API did not return an array. Response: {}", jsonResponse);
            }
        } else if (responseCode == 403) {
            log.error("GitHub API rate limit exceeded (HTTP 403)");
            log.error("Consider adding a GitHub token to increase rate limit from 60 to 5000 requests/hour");
        } else if (responseCode == 404) {
            log.error("GitHub folder not found (HTTP 404): {}", folderPath);
            log.error("Check that the folder path is correct in your repository");
        } else {
            log.error("GitHub API returned error code: {}", responseCode);

            // Try to read error message
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    errorResponse.append(line);
                }
                log.error("Error response: {}", errorResponse.toString());
            } catch (Exception e) {
                log.debug("Could not read error stream", e);
            }
        }

        conn.disconnect();

        log.info("Total files found: {}", fileNames.size());

        return fileNames;
    }

    /**
     * Download a single file from GitHub.
     */
    private boolean downloadFile(String folderPath, String fileName, String localDir, String localFileName) throws Exception {
        String fileUrl = gitHubConfig.getRawFileUrl(folderPath + "/" + fileName);
        String localFilePath = localDir + File.separator + localFileName;

        // Check if file already exists
        File localFile = new File(localFilePath);
        if (localFile.exists()) {
            log.debug("File already exists locally, skipping: {}", localFileName);
            return false;
        }

        log.info("Downloading file: {} from {}", fileName, fileUrl);

        HttpURLConnection conn = (HttpURLConnection) new URL(fileUrl).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Loader-Microservice");

        // Add token if provided
        if (gitHubConfig.getToken() != null && !gitHubConfig.getToken().isEmpty()) {
            conn.setRequestProperty("Authorization", "token " + gitHubConfig.getToken());
        }

        int responseCode = conn.getResponseCode();

        if (responseCode == 200) {
            // Download file
            try (FileOutputStream outputStream = new FileOutputStream(localFilePath)) {
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = conn.getInputStream().read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            log.info("Successfully downloaded: {} -> {}", fileName, localFileName);
            conn.disconnect();
            return true;

        } else {
            log.error("Failed to download file: {} (HTTP {})", fileName, responseCode);
            conn.disconnect();
            return false;
        }
    }

    /**
     * Get the GitHub folder path for a provider.
     * Matches the exact folder names in your repository.
     */
    private String getProviderFolderPath(Provider provider) {
        switch (provider) {
            case GITHUB:
                return "gitHub";  // Note: capital H as in your repo
            case JIRA:
                return "jira";
            case CLICKUP:
                return "clickUp";  // Note: capital U as in your repo
            default:
                return provider.getValue();
        }
    }
}